package me.weishu.kernelsu.ui.screen.shizuku

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.R
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import me.weishu.kernelsu.ui.component.AppBackground

@Composable
internal fun ShizukuMiuix(
    state: ShizukuState,
    bottomInnerPadding: Dp,
    onRequestPermission: () -> Unit,
    onOpenApp: () -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                color = Color.Transparent,
                title = stringResource(R.string.shizuku),
                scrollBehavior = scrollBehavior,
            )
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
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
                    BasicComponent(
                        title = stringResource(R.string.shizuku),
                        summary = stringResource(R.string.shizuku_summary),
                    )
                    ShizukuStatusRow(
                        label = stringResource(R.string.shizuku_label_installed),
                        value = stringResource(
                            if (state.installed) R.string.shizuku_installed else R.string.shizuku_not_installed
                        ),
                        positive = state.installed,
                    )
                    ShizukuStatusRow(
                        label = stringResource(R.string.shizuku_label_running),
                        value = stringResource(
                            if (state.running) R.string.shizuku_running else R.string.shizuku_not_running
                        ),
                        positive = state.running,
                    )
                    ShizukuStatusRow(
                        label = stringResource(R.string.shizuku_label_permission),
                        value = stringResource(
                            if (state.granted) R.string.shizuku_granted else R.string.shizuku_denied
                        ),
                        positive = state.granted,
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                ) {
                    BasicComponent(
                        title = stringResource(R.string.shizuku_request_permission),
                        onClick = onRequestPermission,
                    )
                    BasicComponent(
                        title = stringResource(R.string.shizuku_start),
                        summary = stringResource(R.string.shizuku_start_summary),
                        onClick = onOpenApp,
                    )
                    BasicComponent(
                        title = stringResource(R.string.shizuku_open_app),
                        onClick = onOpenApp,
                    )
                }
            }
        }
    
            }
}
}

@Composable
private fun ShizukuStatusRow(
    label: String,
    value: String,
    positive: Boolean,
) {
    BasicComponent(
        title = label,
        endActions = {
            Text(
                text = value,
                color = if (positive) colorScheme.primary else colorScheme.onSurfaceVariantActions,
            )
        },
    )
}
