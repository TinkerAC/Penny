package app.penny.core.data.repository

import app.penny.core.domain.enum.AppDisplayMode
import app.penny.core.domain.enum.AppThemeContrast
import app.penny.feature.setting.Language
import app.penny.presentation.ui.theme.AppTheme

interface UserPreferenceRepository {



    fun getAppTheme(): AppTheme
    fun setAppTheme(appTheme: AppTheme)

    fun getStoredDynamicTheme(): AppTheme.DynamicAppTheme


    fun getConstraints(): AppThemeContrast
    fun setConstraints(themeContrast: AppThemeContrast)

    fun getDisplayMode(): AppDisplayMode
    fun setDisplayMode(displayMode: AppDisplayMode)

    fun setLanguage(language: Language)
    fun getLanguage(): Language


}