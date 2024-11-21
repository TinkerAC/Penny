package app.penny.presentation.ui.screens.profile

sealed class ProfileIntent {
    object TryLogin : ProfileIntent()
}