// file: core/domain/exception/RegisterException.kt
package app.penny.core.domain.exception

sealed class RegisterException : Exception() {
    object EmailAlreadyRegisteredException : RegisterException()
    object NetworkException : RegisterException()
    class UnknownException(cause: Throwable? = null) : RegisterException()
    object ServerException : RegisterException()
    object PasswordNotMatchException : RegisterException()
}