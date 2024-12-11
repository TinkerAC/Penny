package app.penny

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.penny.core.data.repository.UserDataRepository
import app.penny.di.getKoinInstance
import kotlinx.coroutines.coroutineScope

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