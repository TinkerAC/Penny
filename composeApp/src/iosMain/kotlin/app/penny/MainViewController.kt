package app.penny

import App
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController

import app.penny.platform.disableUiKitOverscroll

import app.penny.platform.ApplicationInitializer
import app.penny.platform.initialize

fun MainViewController() = ComposeUIViewController {
    disableUiKitOverscroll()
    // make sure to initialize the app only once
    remember {
        ApplicationInitializer().initialize()
    }
    App()

}
