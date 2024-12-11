package app.penny.core.domain.exception

sealed class AppException(message: String) : Throwable(message) {
    data class NetworkException(override val cause: Throwable) : AppException("Network error: ${cause.message}")
    object TimeoutException : AppException("Request timed out, please try again later")
    data class BusinessException(val code: Int, override val message: String) :
        AppException("Business error: $message (Code: $code)")
    data class DataException(val reason: String) : AppException("Local data error: $reason")
    data class UnknownException(override val cause: Throwable) : AppException("Unknown error: ${cause.message}")
}