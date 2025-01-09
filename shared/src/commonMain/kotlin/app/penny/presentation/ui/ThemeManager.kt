package app.penny.presentation.ui

import app.penny.presentation.enumerate.AppDisplayMode
import app.penny.presentation.enumerate.AppThemeContrast
import app.penny.presentation.ui.theme.AppTheme

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


data class ThemeState(
    val appTheme: AppTheme,
    val displayMode: AppDisplayMode,
    val constraints: AppThemeContrast
)


object ThemeManager {
    private val _themeChanges = MutableSharedFlow<ThemeState>() // 管理主题变更事件

    val themeChanges = _themeChanges.asSharedFlow()

    suspend fun notifyThemeChange(newThemeState: ThemeState) {
        _themeChanges.emit(newThemeState) // 通知新主题状态
    }
}
