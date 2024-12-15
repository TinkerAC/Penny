package app.penny.core.domain.exception

sealed class LoginException(message: String) : Throwable(message) {
    object InvalidCredentialsException : LoginException("Invalid email or password")
    object NetworkException : LoginException("Network connection failed")
    object ServerException : LoginException("Server encountered an error")
    data class UnknownException(override val cause: Throwable) :
        LoginException("Unknown error: ${cause.message}")
}