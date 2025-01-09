package app.penny.core.data.repository.impl

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import app.penny.core.data.kvstore.UserPreferenceManager
import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.presentation.enumerate.AppDisplayMode
import app.penny.presentation.enumerate.AppThemeContrast
import app.penny.presentation.enumerate.Language
import app.penny.presentation.enumerate.ThemeMode
import app.penny.presentation.ui.theme.AppTheme
import kotlin.random.Random

class UserPreferenceRepositoryImpl(
    private val userPreferenceManager: UserPreferenceManager
) : UserPreferenceRepository {
    override fun getAppTheme(): AppTheme {
        val themeMode: ThemeMode = ThemeMode.valueOf(
            userPreferenceManager.getStringOrNull(UserPreferenceManager.THEME_MODE)
                ?: ThemeMode.STATIC.name
        )

        return when (themeMode) {
            ThemeMode.STATIC -> {
                val staticThemeName =
                    userPreferenceManager.getStringOrNull(UserPreferenceManager.STATIC_THEME_NAME)
                AppTheme.Static.fromPersistentName(staticThemeName ?: "")
            }

            ThemeMode.DYNAMIC -> {
                // 根据seedColor创建DynamicTheme对象（需根据实际实现调整）
                val seedColorArgb =
                    userPreferenceManager.getStringOrNull(UserPreferenceManager.DYNAMIC_THEME_SEED_COLOR_ARGB)
                AppTheme.DynamicAppTheme(Color(seedColorArgb?.toIntOrNull() ?: 0))
            }

        }
    }

    override fun setAppTheme(appTheme: AppTheme) {
        when (appTheme) {
            is AppTheme.Static -> {
                // 将静态主题模式与名称保存
                userPreferenceManager.putString(UserPreferenceManager.THEME_MODE, "STATIC")
                userPreferenceManager.putString(
                    UserPreferenceManager.STATIC_THEME_NAME,
                    appTheme.persistentName
                )
            }

            is AppTheme.DynamicAppTheme -> {
                // 将动态主题模式与seed颜色保存
                userPreferenceManager.putString(
                    UserPreferenceManager.THEME_MODE,
                    ThemeMode.DYNAMIC.name
                )
                userPreferenceManager.putString(
                    UserPreferenceManager.DYNAMIC_THEME_SEED_COLOR_ARGB,
                    appTheme.seedColor.toArgb().toString()
                )

            }
        }
    }

    override fun getConstraints(): AppThemeContrast {
        return AppThemeContrast.valueOf(
            userPreferenceManager.getStringOrNull(UserPreferenceManager.CONSTRAINTS)
                ?: AppThemeContrast.MEDIUM.name
        )
    }

    override fun setConstraints(themeContrast: AppThemeContrast) {
        userPreferenceManager.putString(UserPreferenceManager.CONSTRAINTS, themeContrast.name)
    }

    override fun getDisplayMode(): AppDisplayMode {
        return AppDisplayMode.valueOf(
            userPreferenceManager.getStringOrNull(UserPreferenceManager.DARK_MODE)
                ?: AppDisplayMode.SYSTEM.name
        )
    }

    override fun setDisplayMode(displayMode: AppDisplayMode) {
        userPreferenceManager.putString(UserPreferenceManager.DARK_MODE, displayMode.name)
    }

    override fun setLanguage(language: Language) {
        userPreferenceManager.putString(UserPreferenceManager.LANGUAGE, language.name)
    }

    override fun getLanguage(): Language {
        return Language.valueOf(
            userPreferenceManager.getStringOrNull(UserPreferenceManager.LANGUAGE)
                ?: Language.ENGLISH.name
        )
    }


    override fun getStoredDynamicTheme(): AppTheme.DynamicAppTheme {
        val seedColorArgb =
            userPreferenceManager.getStringOrNull(UserPreferenceManager.DYNAMIC_THEME_SEED_COLOR_ARGB)
        //if not set , return random color

        return when (seedColorArgb) {
            null -> AppTheme.DynamicAppTheme(
                Color(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat()
                )
            )

            else -> AppTheme.DynamicAppTheme(Color(seedColorArgb.toInt()))
        }

    }


    override fun getNotificationEnabled(): Boolean {
        return userPreferenceManager.getBoolean(UserPreferenceManager.NOTIFICATION_ENABLED)
    }

    override fun setNotificationEnabled(enabled: Boolean) {
        userPreferenceManager.putBoolean(UserPreferenceManager.NOTIFICATION_ENABLED, enabled)
    }

    override fun getScheduledNotificationEnabled(): Boolean {
        return userPreferenceManager.getBoolean(UserPreferenceManager.SCHEDULED_NOTIFICATION_ENABLED)
    }

    override fun setScheduledNotificationEnabled(enabled: Boolean) {
        userPreferenceManager.putBoolean(
            UserPreferenceManager.SCHEDULED_NOTIFICATION_ENABLED,
            enabled
        )
    }

    override fun getBudgetReachedNotificationEnabled(): Boolean {
        return userPreferenceManager.getBoolean(UserPreferenceManager.BUDGET_REACHED_NOTIFICATION_ENABLED)
    }

    override fun setBudgetReachedNotificationEnabled(enabled: Boolean) {
        userPreferenceManager.putBoolean(
            UserPreferenceManager.BUDGET_REACHED_NOTIFICATION_ENABLED,
            enabled
        )
    }
}

