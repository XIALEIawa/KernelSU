package me.weishu.kernelsu.ui.screen.features

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.util.NativeSettingsLauncher

/**
 * One row in the feature area.
 *
 * Features are grouped here instead of being scattered over the settings page, so a device that
 * restricts some capabilities can still expose everything it does support in one place.
 */
data class FeatureEntry(
    val id: String,
    val title: String,
    val summary: String,
    val onClick: () -> Unit,
)

private enum class FeaturePage { List, ApkInstall, Ota }

@Composable
fun FeaturesPager(bottomInnerPadding: Dp) {
    val context = LocalContext.current
    var page by remember { mutableStateOf(FeaturePage.List) }

    BackHandler(enabled = page != FeaturePage.List) { page = FeaturePage.List }

    when (page) {
        FeaturePage.ApkInstall -> {
            ApkInstallPage(bottomInnerPadding = bottomInnerPadding, onBack = { page = FeaturePage.List })
            return
        }

        FeaturePage.Ota -> {
            OtaPage(bottomInnerPadding = bottomInnerPadding, onBack = { page = FeaturePage.List })
            return
        }

        else -> Unit
    }

    val entries = listOf(
        FeatureEntry(
            id = "native_settings",
            title = stringResource(R.string.feature_native_settings),
            summary = stringResource(R.string.feature_native_settings_summary),
            onClick = {
                if (!NativeSettingsLauncher.open(context)) {
                    Toast.makeText(
                        context,
                        R.string.feature_native_settings_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
        ),
        FeatureEntry(
            id = "install_apk",
            title = stringResource(R.string.feature_install_apk),
            summary = stringResource(R.string.feature_install_apk_summary),
            onClick = { page = FeaturePage.ApkInstall },
        ),
        FeatureEntry(
            id = "ota_download",
            title = stringResource(R.string.feature_ota),
            summary = stringResource(R.string.feature_ota_summary),
            onClick = { page = FeaturePage.Ota },
        ),
    )

    when (LocalUiMode.current) {
        UiMode.Miuix -> FeaturesMiuix(entries = entries, bottomInnerPadding = bottomInnerPadding)
        UiMode.Material -> FeaturesMaterial(entries = entries, bottomInnerPadding = bottomInnerPadding)
    }
}
