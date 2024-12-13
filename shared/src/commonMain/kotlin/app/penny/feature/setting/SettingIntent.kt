package app.penny.feature.setting

import app.penny.presentation.ui.theme.DisplayMode
import app.penny.presentation.ui.theme.ThemeColor
import app.penny.presentation.ui.theme.ThemeConstraint

sealed class SettingIntent {
    data class SetTheme(val themeName: ThemeColor) : SettingIntent()
    data class SetConstraints(val constraints: ThemeConstraint) : SettingIntent()
    data class SetDisplayMode(val darkMode: DisplayMode) : SettingIntent()
}
