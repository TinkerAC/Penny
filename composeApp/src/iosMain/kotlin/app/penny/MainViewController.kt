package app.penny

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    getPlatform()
    disableUiKitOverscroll()

    val initializer = remember {
        ApplicationInitializer(
        ).initialize()
    }


    App()

}
