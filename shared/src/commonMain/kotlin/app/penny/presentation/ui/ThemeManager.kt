package app.penny.presentation.ui
import app.penny.presentation.ui.theme.DisplayMode
import app.penny.presentation.ui.theme.ThemeConfig
import app.penny.presentation.ui.theme.ThemeConstraint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


data class ThemeState(
    val themeConfig: ThemeConfig,
    val displayMode: DisplayMode,
    val constraints: ThemeConstraint
)

object ThemeManager {
    private val _themeChanges = MutableSharedFlow<ThemeState>() // 管理主题变更事件
    val themeChanges = _themeChanges.asSharedFlow()

    suspend fun notifyThemeChange(newThemeState: ThemeState) {
        _themeChanges.emit(newThemeState) // 通知新主题状态
    }
}
