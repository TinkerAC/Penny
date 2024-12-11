package app.penny

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import app.penny.feature.onBoarding.OnboardingScreen
import app.penny.presentation.ui.MainScreen
import app.penny.presentation.ui.theme.AppTheme
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import co.touchlab.kermit.Logger
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    val firstTime = false
    AppTheme {
        // root Navigator，用于管理屏幕导航
        Navigator(
            screen = if (firstTime) OnboardingScreen() else MainScreen()
        ) {
            navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}