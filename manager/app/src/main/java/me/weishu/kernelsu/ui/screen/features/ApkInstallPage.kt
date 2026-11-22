package me.weishu.kernelsu.ui.screen.features

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.R
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import java.io.File

/**
 * "Install APK" feature: pick an apk and push it through su.
 *
 * znxxinstall is a placeholder for now.
 */
@Composable
internal fun ApkInstallPage(bottomInnerPadding: Dp, onBack: () -> Unit) {
    var stage by remember { mutableIntStateOf(0) } // 0 = menu, 1 = root, 2 = znxx
    var selected by remember { mutableStateOf<Uri?>(null) }
    var result by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri -> if (uri != null) selected = uri }

    BackHandler(enabled = stage != 0) { stage = 0 }

    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        topBar = { TopAppBar(title = stringResource(R.string.feature_install_apk), scrollBehavior = scrollBehavior) },
        popupHost = { },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + bottomInnerPadding,
            ),
        ) {
            item {
                Card(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                ) {
                    BasicComponent(title = stringResource(R.string.feature_back), onClick = onBack)
                    when (stage) {
                        0 -> {
                            BasicComponent(
                                title = stringResource(R.string.install_via_root),
                                onClick = { stage = 1 },
                            )
                            BasicComponent(
                                title = stringResource(R.string.install_via_znxx),
                                onClick = { stage = 2 },
                            )
                        }

                        1 -> {
                            BasicComponent(
                                title = stringResource(R.string.install_pick_apk),
                                summary = selected?.lastPathSegment
                                    ?: stringResource(R.string.install_none_selected),
                                onClick = {
                                    picker.launch(arrayOf("application/vnd.android.package-archive"))
                                },
                            )
                            BasicComponent(
                                title = if (running) {
                                    stringResource(R.string.install_running)
                                } else {
                                    stringResource(R.string.install_start)
                                },
                                onClick = {
                                    val uri = selected ?: return@BasicComponent
                                    if (running) return@BasicComponent
                                    running = true
                                    result = ""
                                    scope.launch {
                                        val message = installApk(context, uri)
                                        result = message
                                        running = false
                                    }
                                },
                            )
                            if (result.isNotBlank()) {
                                BasicComponent(title = result)
                            }
                        }

                        else -> {
                            BasicComponent(
                                title = stringResource(R.string.install_via_znxx),
                                summary = stringResource(R.string.feature_placeholder),
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun installApk(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
    try {
        val cache = File(context.cacheDir, "talpad_install.apk")
        context.contentResolver.openInputStream(uri)?.use { input ->
            cache.outputStream().use { output -> input.copyTo(output) }
        } ?: return@withContext "cannot read apk"

        val remote = "/data/local/tmp/talpad_install.apk"
        val result = Shell.cmd("cp '${cache.absolutePath}' $remote", "pm install -r $remote").exec()
        cache.delete()
        (result.out + result.err).joinToString("\n").trim().ifBlank { "done" }
    } catch (t: Throwable) {
        t.message ?: t.toString()
    }
}

@Composable
internal fun OtaPage(bottomInnerPadding: Dp, onBack: () -> Unit) {
    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        topBar = { TopAppBar(title = stringResource(R.string.feature_ota), scrollBehavior = scrollBehavior) },
        popupHost = { },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + bottomInnerPadding,
            ),
        ) {
            item {
                Card(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                ) {
                    BasicComponent(title = stringResource(R.string.feature_back), onClick = onBack)
                    BasicComponent(title = stringResource(R.string.feature_placeholder))
                }
            }
        }
    }
}
