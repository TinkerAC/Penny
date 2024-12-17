package app.penny

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

//JVM entry point
fun main() = application {

    getPlatform()

    ApplicationInitializer(
        application = this
    )
        .initialize()


    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(SharedRes.strings.app_name),
        state = rememberWindowState(
        )
    ) {
        App()
    }
}