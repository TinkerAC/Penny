package app.penny.core.data.repository

import app.penny.presentation.ui.theme.DisplayMode
import app.penny.presentation.ui.theme.ThemeColor
import app.penny.presentation.ui.theme.ThemeConstraint

interface UserPreferenceRepository {
    fun getThemeColor(): ThemeColor
    fun setThemeColor(themeColor: ThemeColor)

    fun getConstraints(): ThemeConstraint
    fun setConstraints(themeConstraint: ThemeConstraint)

    fun getDisplayMode(): DisplayMode
    fun setDisplayMode(displayMode: DisplayMode)



}