package app.penny.feature.setting

import app.penny.presentation.enumerate.AppDisplayMode
import app.penny.presentation.enumerate.AppThemeContrast
import app.penny.presentation.enumerate.Language
import app.penny.presentation.ui.theme.AppTheme

data class SettingUiState(
    val appTheme: AppTheme = AppTheme.Static.items.first(),
    val appThemes: List<AppTheme>? = null,

    val displayMode: AppDisplayMode = AppDisplayMode.SYSTEM,
    val displayModes: List<AppDisplayMode> = AppDisplayMode.entries,

    val constraint: AppThemeContrast = AppThemeContrast.HIGH,
    val constraints: List<AppThemeContrast> = AppThemeContrast.entries,

    val language: Language = Language.entries.first(),
    val languages: List<Language> = Language.entries,

    val showColorPicker: Boolean = false,


    val autoCloudSyncEnabled: Boolean = false,
)