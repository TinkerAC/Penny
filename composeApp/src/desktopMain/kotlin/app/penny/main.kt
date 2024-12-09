package app.penny

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

//JVM entry point
fun main() = application {

    ApplicationInitializer(
        application = this
    )
        .initialize()


    Window(
        onCloseRequest = ::exitApplication,
        title = "Penny",
        state = rememberWindowState(
        )
    ) {
        App()
    }
}