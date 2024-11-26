package app.penny

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.penny.di.initKoin
import co.touchlab.kermit.Logger
import app.penny.core.data.kvstore.TokenManager
import org.koin.java.KoinJavaComponent.inject

//JVM entry point
fun main() = application {

    ApplicationInitializer(
        application = this)
        .initKoin()
        .initSession()


    Window(
        onCloseRequest = ::exitApplication,
        title = "Penny",
        state = rememberWindowState(


        )
    ) {
        App()
    }
}