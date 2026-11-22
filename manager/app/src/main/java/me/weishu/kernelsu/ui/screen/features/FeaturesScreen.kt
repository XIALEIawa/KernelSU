package me.weishu.kernelsu.ui.screen.features

import android.widget.Toast
import androidx.compose.runtime.Composable
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

@Composable
fun FeaturesPager(bottomInnerPadding: Dp) {
    val context = LocalContext.current

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
    )

    when (LocalUiMode.current) {
        UiMode.Miuix -> FeaturesMiuix(entries = entries, bottomInnerPadding = bottomInnerPadding)
        UiMode.Material -> FeaturesMaterial(entries = entries, bottomInnerPadding = bottomInnerPadding)
    }
}
