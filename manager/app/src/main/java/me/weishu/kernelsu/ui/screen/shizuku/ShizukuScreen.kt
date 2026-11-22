package me.weishu.kernelsu.ui.screen.shizuku

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode
import rikka.shizuku.Shizuku

internal const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"

/** Snapshot of the Shizuku side, refreshed whenever the page is resumed. */
internal data class ShizukuState(
    val installed: Boolean = false,
    val running: Boolean = false,
    val granted: Boolean = false,
)

internal fun detectShizuku(context: Context): ShizukuState {
    val installed = runCatching {
        context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
    }.isSuccess
    val running = runCatching { Shizuku.pingBinder() }.getOrDefault(false)
    val granted = running && runCatching {
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)
    return ShizukuState(installed = installed, running = running, granted = granted)
}

internal fun openShizuku(context: Context) {
    runCatching {
        context.packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE)
    }.getOrNull()?.let { intent ->
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

/**
 * Shizuku is used as the secondary execution channel: on devices where KernelSU cannot do
 * something, the same action can still go through the ADB-level shell Shizuku provides.
 */
@Composable
fun ShizukuPager(bottomInnerPadding: Dp) {
    val context = LocalContext.current
    var state by remember { mutableStateOf(detectShizuku(context)) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                state = detectShizuku(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val onRequestPermission = {
        runCatching { Shizuku.requestPermission(0) }
            .onFailure {
                Toast.makeText(context, R.string.shizuku_not_running, Toast.LENGTH_SHORT).show()
            }
        state = detectShizuku(context)
    }
    val onOpenApp = { openShizuku(context) }

    when (LocalUiMode.current) {
        UiMode.Miuix -> ShizukuMiuix(state, bottomInnerPadding, onRequestPermission, onOpenApp)
        UiMode.Material -> ShizukuMaterial(state, bottomInnerPadding, onRequestPermission, onOpenApp)
    }
}
