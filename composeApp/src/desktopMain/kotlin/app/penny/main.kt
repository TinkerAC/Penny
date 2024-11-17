package app.penny

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.penny.di.initKoin
import co.touchlab.kermit.Logger


fun main() = application {
    initKoin()

    Logger.i("Koin initialized")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Penny",
        state = rememberWindowState(
            width = 600.dp,
            height = 1200.dp,

        )
    ) {
        App()
    }
}