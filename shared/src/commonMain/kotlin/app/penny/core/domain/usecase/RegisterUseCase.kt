// file: core/domain/usecase/RegisterUseCase.kt
package app.penny.core.domain.usecase

import app.penny.core.data.repository.AuthRepository
import app.penny.core.domain.exception.RegisterException
import app.penny.servershared.dto.responseDto.RegisterResponse
import co.touchlab.kermit.Logger
import kotlinx.io.IOException

class RegisterUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        uuid: String?
    ): RegisterResponse {

        if (password != confirmPassword) {
            throw RegisterException.PasswordNotMatchException
        }

        try {
            // 检查邮箱是否已注册
            val isEmailRegistered = authRepository.checkIsEmailRegistered(email)
            if (isEmailRegistered == true) {
                // 如果已知邮箱已注册，直接抛出异常
                throw RegisterException.EmailAlreadyRegisteredException
            } else if (isEmailRegistered == null) {
                // 无法确认邮箱状态，可能是网络错误
                throw RegisterException.NetworkException
            }

            // 调用注册接口
            val response = authRepository.register(email, password, uuid)
            if (!response.success) {
                // 服务端返回错误信息或结果不成功
                Logger.e("Register failed with message: ${response.message}")
                throw RegisterException.ServerException
            }

            // 注册成功返回响应
            return response
        } catch (e: IOException) {
            // 网络层面IO错误
            Logger.e("Network error during register", e)
            throw RegisterException.NetworkException
        } catch (e: RegisterException) {
            // 已知注册异常则直接抛出
            throw e
        } catch (e: Exception) {
            // 其它未知异常
            Logger.e("Unexpected error during register", e)
            throw RegisterException.UnknownException(e)
        }
    }
}