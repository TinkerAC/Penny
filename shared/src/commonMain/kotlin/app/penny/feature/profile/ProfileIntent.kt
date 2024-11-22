package app.penny.feature.profile

sealed class ProfileIntent {
    object TryLogin : ProfileIntent()
    class Login(val username: String, val password: String) : ProfileIntent()
    class UnfocusUsername(val username: String) : ProfileIntent()
}
