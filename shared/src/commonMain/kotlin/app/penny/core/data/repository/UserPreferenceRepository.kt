package app.penny.core.data.repository

import app.penny.presentation.enumerate.AppDisplayMode
import app.penny.presentation.enumerate.AppThemeContrast
import app.penny.presentation.enumerate.Language
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

    //Notification preferences
    fun getNotificationEnabled(): Boolean
    fun setNotificationEnabled(enabled: Boolean)

    fun getScheduledNotificationEnabled(): Boolean
    fun setScheduledNotificationEnabled(enabled: Boolean)

    fun getBudgetReachedNotificationEnabled(): Boolean
    fun setBudgetReachedNotificationEnabled(enabled: Boolean)


}