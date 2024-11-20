package app.penny

import androidx.compose.ui.window.ComposeUIViewController
import app.penny.di.initKoin



fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()

}
