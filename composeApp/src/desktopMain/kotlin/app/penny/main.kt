package app.penny

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.penny.di.initKoin
import kotlin.math.log

fun main() = application {
    initKoin()
    println("Koin initialized")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Penny",
    ) {

        App()
    }
}