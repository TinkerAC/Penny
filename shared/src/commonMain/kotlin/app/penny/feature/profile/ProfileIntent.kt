package app.penny.feature.profile

sealed class ProfileIntent {
    object TryLogin : ProfileIntent()
    object DismissLoginModal : ProfileIntent()
    class Login(val username: String, val password: String) : ProfileIntent()
    class UnfocusEmail(val username: String) : ProfileIntent()
}