package me.weishu.kernelsu.ui.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Opens the stock Android settings entry that some ROMs hide behind an "external" fragment.
 *
 * The list of candidate entries is tried in order: the first one the platform can resolve wins,
 * the others raise [android.content.ActivityNotFoundException] on devices that don't ship them.
 */
object NativeSettingsLauncher {
    private const val TAG = "NativeSettings"
    private const val SETTINGS_PACKAGE = "com.android.settings"

    private const val EXTRA_SHOW_FRAGMENT = ":settings:show_fragment"
    private const val EXTRA_COMPONENT_PACKAGE = ":settings:component_package"
    private const val EXTRA_COMPONENT_CLASS = ":settings:component_class"

    private const val EXTERNAL_FRAGMENT = "com.android.xpadsettings.widget.menu.XpadExternalEnterFragment"
    private const val TARGET_ACTIVITY = "com.android.settings.homepage.SettingsHomepageActivity"

    private val CANDIDATE_ENTRIES = listOf(
        "com.android.xpadsettings.XpadSettings\$Companion\$XpadDisplayActivity",
        "com.android.xpadsettings.XpadSettings\$Companion\$XpadScreenRefreshRateActivity",
        "com.android.xpadsettings.XpadSettings\$Companion\$XpadLockScreenTimeActivity",
        "com.android.xpadsettings.XpadSettings\$Companion\$XpadIntentInterceptor",
        "com.android.xpadsettings.XpadSettings\$Companion\$XpadAppMetaDetailActivity",
        "com.android.xpadsettings.XpadSettings\$Companion\$XpadNotificationDetailActivity",
    )

    /** @return true when one of the candidate entries was launched. */
    fun open(context: Context): Boolean {
        CANDIDATE_ENTRIES.forEach { entry ->
            val intent = Intent().apply {
                component = ComponentName(SETTINGS_PACKAGE, entry)
                putExtra(EXTRA_SHOW_FRAGMENT, EXTERNAL_FRAGMENT)
                putExtra(EXTRA_COMPONENT_PACKAGE, SETTINGS_PACKAGE)
                putExtra(EXTRA_COMPONENT_CLASS, TARGET_ACTIVITY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
                return true
            } catch (t: Throwable) {
                Log.w(TAG, "cannot launch $entry", t)
            }
        }
        return false
    }
}
