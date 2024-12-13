package app.penny.feature.setting

import app.penny.presentation.ui.theme.DisplayMode
import app.penny.presentation.ui.theme.ThemeColor
import app.penny.presentation.ui.theme.ThemeConstraint

data class SettingUiState(
    val theme: ThemeColor = ThemeColor.THEME1,
    val themes : List<ThemeColor> =ThemeColor.entries,

    val displayMode: DisplayMode = DisplayMode.SYSTEM,
    val displayModes : List<DisplayMode> = DisplayMode.entries,

    val constraint: ThemeConstraint = ThemeConstraint.MEDIUM,
    val constraints : List<ThemeConstraint> = ThemeConstraint.entries
)