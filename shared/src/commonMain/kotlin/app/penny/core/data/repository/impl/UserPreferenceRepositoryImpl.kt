package app.penny.core.data.repository.impl

import app.penny.core.data.kvstore.UserPreferenceManager
import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.presentation.ui.theme.DisplayMode
import app.penny.presentation.ui.theme.ThemeColor
import app.penny.presentation.ui.theme.ThemeConstraint

class UserPreferenceRepositoryImpl(
    private val settingManager: UserPreferenceManager
) : UserPreferenceRepository {
    override fun getThemeColor(): ThemeColor {
        return ThemeColor.valueOf(
            settingManager.getStringOrNull(UserPreferenceManager.THEME_NAME)
                ?: ThemeColor.THEME1.name
        )
    }

    override fun setThemeColor(themeColor: ThemeColor) {
        settingManager.putString(
            UserPreferenceManager.THEME_NAME,
            themeColor.name
        )
    }

    override fun getConstraints(): ThemeConstraint {
        return ThemeConstraint.valueOf(
            settingManager.getStringOrNull(UserPreferenceManager.CONSTRAINTS)
                ?: ThemeConstraint.MEDIUM.name
        )
    }

    override fun setConstraints(themeConstraint: ThemeConstraint) {
        settingManager.putString(UserPreferenceManager.CONSTRAINTS, themeConstraint.name)
    }

    override fun getDisplayMode(): DisplayMode {
        return DisplayMode.valueOf(
            settingManager.getStringOrNull(UserPreferenceManager.DARK_MODE)
                ?: DisplayMode.SYSTEM.name
        )
    }

    override fun setDisplayMode(displayMode: DisplayMode) {
        settingManager.putString(UserPreferenceManager.DARK_MODE, displayMode.name)
    }
}