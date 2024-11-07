package app.penny

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import app.penny.presentation.ui.MainScreen
import app.penny.presentation.ui.theme.AppTheme
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import co.touchlab.kermit.Logger
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(
) {


    Logger.i("isSystemInDarkTheme: ${isSystemInDarkTheme()}")


    AppTheme {
        // 顶级 Navigator,可用于实现一些覆盖全屏的功能
        Navigator(MainScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}