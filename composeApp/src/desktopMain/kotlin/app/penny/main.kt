package app.penny

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.penny.di.initKoin
import co.touchlab.kermit.Logger


fun main() = application {
    initKoin()

    Logger.i("Koin initialized")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Penny",
    ) {
        App()
    }
}