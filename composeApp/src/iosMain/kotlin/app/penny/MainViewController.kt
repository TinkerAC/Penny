package app.penny

import App
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    disableUiKitOverscroll()
    // make sure to initialize the app only once
    remember {
        ApplicationInitializer().initialize()
    }
    App()

}
