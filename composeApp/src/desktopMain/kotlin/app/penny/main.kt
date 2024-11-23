package app.penny

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.penny.di.initKoin
import co.touchlab.kermit.Logger

//JVM entry point
fun main() = application {
    initKoin()

    Logger.i("Koin initialized")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Penny",
        state = rememberWindowState(


        )
    ) {
        App()
    }
}