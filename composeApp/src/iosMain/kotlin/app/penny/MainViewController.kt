package app.penny

import androidx.compose.ui.window.ComposeUIViewController


fun MainViewController() = ComposeUIViewController {

    ApplicationInitializer(
    ).initKoin()
        .initSession()

    App()

}
