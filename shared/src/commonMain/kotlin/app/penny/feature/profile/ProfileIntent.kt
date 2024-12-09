package app.penny.feature.profile

sealed class ProfileIntent {
    object TryLogin : ProfileIntent()
    object DismissLoginModal : ProfileIntent()
    class Login(val email: String, val password: String) : ProfileIntent()
    class Register(val email: String, val password: String) : ProfileIntent()

    class UnfocusEmail(val email: String) : ProfileIntent()

    object ToggleModalMode : ProfileIntent()

    class InputEmail(val email: String) : ProfileIntent()
}