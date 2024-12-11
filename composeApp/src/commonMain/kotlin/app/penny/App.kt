package app.penny

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.penny.core.data.repository.UserDataRepository
import app.penny.di.getKoinInstance
import app.penny.feature.onBoarding.OnboardingNavigatorScreen
import app.penny.presentation.ui.MainScreen
import app.penny.presentation.ui.theme.AppTheme
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import co.touchlab.kermit.Logger
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val userDataRepository = getKoinInstance<UserDataRepository>()
    var isFirstTime by remember { mutableStateOf<Boolean?>(null) }

    // 使用 LaunchedEffect 调用挂起方法
    LaunchedEffect(Unit) {
        isFirstTime = userDataRepository.getIsFirstTime()
    }

    // 等待加载完成
    if (isFirstTime == null) {
        LoadingScreen() // 显示加载界面
    } else {
        AppTheme {
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

@Composable
fun LoadingScreen() {
    // 显示加载中的界面
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}