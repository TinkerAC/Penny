package app.penny

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.di.getKoinInstance
import app.penny.feature.onBoarding.OnboardingNavigatorScreen
import app.penny.presentation.ui.MainScreen
import app.penny.presentation.ui.ThemeManager
import app.penny.presentation.ui.ThemeState
import app.penny.presentation.ui.theme.AppTheme
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition


@Composable
fun App() {

    val userPreferenceRepository = getKoinInstance<UserPreferenceRepository>()

    // 当前主题状态
    var themeState by remember {
        mutableStateOf(
            ThemeState(
                themeConfig = userPreferenceRepository.getThemeColor().themeConfig,
                displayMode = userPreferenceRepository.getDisplayMode(),
                constraints = userPreferenceRepository.getConstraints()
            )
        )
    }

    val userDataRepository = getKoinInstance<UserDataRepository>()
    var isFirstTime by remember { mutableStateOf<Boolean?>(null) }

    // 使用 LaunchedEffect 初始化数据和监听主题变化
    LaunchedEffect(Unit) {
        // 加载首次启动状态
        isFirstTime = userDataRepository.getIsFirstTime()

        // 监听主题变化事件
        ThemeManager.themeChanges.collect { newThemeState ->
            themeState = newThemeState // 更新主题状态
        }
    }

    // 等待加载完成
    if (isFirstTime == null) {
        CircularProgressIndicator() // 显示加载中
    } else {
        // 应用主题并加载主内容
        AppTheme(
            themeConfig = themeState.themeConfig,
            darkTheme = themeState.displayMode,
            constraints = themeState.constraints
        ) {
            // root Navigator，用于管理屏幕导航
            Navigator(
                screen = if (isFirstTime == true) {
                    OnboardingNavigatorScreen()
                } else {
                    MainScreen()
                }
            ) { navigator ->
                SlideTransition(navigator = navigator)
            }
        }
    }
}