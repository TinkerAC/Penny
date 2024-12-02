package app.penny.feature.profile

sealed class ProfileIntent {
    object TryLogin : ProfileIntent()
    object DismissLoginModal : ProfileIntent()
    class Login(val username: String, val password: String) : ProfileIntent()
    class UnfocusEmail(val username: String) : ProfileIntent()
    object NavigateToSettings : ProfileIntent()
    object NavigateToNotifications : ProfileIntent()
    object NavigateToBadges : ProfileIntent()
    object NavigateToPennyBox : ProfileIntent()
    object NavigateToHelp : ProfileIntent()
    object NavigateToFeedback : ProfileIntent()
    object NavigateToAboutUs : ProfileIntent()
}