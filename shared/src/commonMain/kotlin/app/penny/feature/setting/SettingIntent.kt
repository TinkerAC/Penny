package app.penny.feature.setting

import app.penny.presentation.enumerate.AppDisplayMode
import app.penny.presentation.enumerate.AppThemeContrast
import app.penny.presentation.enumerate.Language
import app.penny.presentation.ui.theme.AppTheme


sealed class SettingIntent {
    data class SetTheme(val appTheme: AppTheme) : SettingIntent()
    data class SetConstraints(val constraints: AppThemeContrast) : SettingIntent()
    data class SetDisplayMode(val darkMode: AppDisplayMode) : SettingIntent()
    data class SetDynamicTheme(val appTheme: AppTheme.DynamicAppTheme) : SettingIntent()
    data object ShowColorPicker : SettingIntent()
    data object HideColorPicker : SettingIntent()
    data class SetLanguage(val language: Language) : SettingIntent()
    data object ToggleAutoCloudSync : SettingIntent()
    data object LogOut : SettingIntent()
}
