package app.penny

import App
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

//JVM entry point
fun main() = application {

    val appInitializer = ApplicationInitializer(
        application = this
    )

    appInitializer.printDeviceInfo()

    appInitializer.initialize()



    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(SharedRes.strings.app_name),
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            width = 450.dp,
            height = 850.dp,
            position = WindowPosition.Aligned(Alignment.Center)
        )
    ) {
        App()
    }
}