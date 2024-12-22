package app.penny.feature.setting

import app.penny.core.domain.enum.AppDisplayMode
import app.penny.core.domain.enum.AppThemeContrast
import app.penny.core.domain.enum.Language
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

    val showColorPicker: Boolean = false
)