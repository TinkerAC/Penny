package app.penny.core.domain.usecase

import app.penny.core.data.enumerate.toUserModel
import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.exception.LoginException
import app.penny.servershared.dto.responseDto.LoginResponse
import co.touchlab.kermit.Logger
import kotlinx.io.IOException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataRepository: UserDataRepository,
    private val syncDataUseCase: SyncDataUseCase,
    private val ledgerRepository: LedgerRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): LoginResponse {

        try {
            // Check if there is a local user (no email present)
            val localUser = userRepository.findByEmailIsNull()
            Logger.d("Local User: $localUser")

            // Attempt login
            val response = authRepository.login(email, password)

            // Handle successful response
            if (response.success) {
                handleSuccessfulLogin(response)
            } else {
                throw LoginException.InvalidCredentialsException
            }


            return response
        } catch (e: IOException) {
            // Handle network-related exceptions
            Logger.e("Network error during login", e)
            throw LoginException.NetworkException
        } catch (e: LoginException) {
            // Rethrow known login exceptions
            throw e
        } catch (e: Exception) {
            // Handle unknown errors
            Logger.e("Unexpected error during login", e)
            throw LoginException.UnknownException(e)
        } finally {
            userDataRepository.setIsFirstTime(false)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun handleSuccessfulLogin(response: LoginResponse) {


        // sync data
        val accessToken = response.accessToken!!
        val refreshToken = response.refreshToken!!
        Logger.d(response.toString())

        // Save tokens
        authRepository.saveAccessToken(accessToken)
        authRepository.saveRefreshToken(refreshToken)


        try {
            //set user data
            userRepository.upsertByUuid(response.userDto!!.toUserModel())
            userDataRepository.setUser(response.userDto.toUserModel())
            userDataRepository.setUserEmail(response.userDto.email)
            response.userDto.username?.let { userDataRepository.setUserName(it) }
            val ledgers = ledgerRepository.findByUserUuid(Uuid.parse(response.userDto.uuid))
            ledgers.firstOrNull()?.let { userDataRepository.setDefaultLedger(it) }
            syncDataUseCase()
        } catch (e: Exception) {
            Logger.e("Error during login", e)
            e.printStackTrace()
        }


    }
}