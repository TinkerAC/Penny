package app.penny.core.domain.usecase

import app.penny.core.data.model.toUserModel
import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.servershared.dto.responseDto.LoginResponse
import co.touchlab.kermit.Logger
import kotlin.uuid.ExperimentalUuidApi

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataRepository: UserDataRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        email: String, password: String
    ): LoginResponse {

        //check if there is a local user(no email present)

        val localUser = userRepository.findByEmailIsNull()
        Logger.d("Local User: $localUser")
        //if there is a local user, and the email is not registered, bind the email to the local user


        val response = authRepository.login(email, password)

        if (response.success) {
            val accessToken = response.accessToken!!
            val refreshToken = response.refreshToken!!
            Logger.d(
                response.toString()
            )
            authRepository.saveAccessToken(accessToken)
            authRepository.saveRefreshToken(refreshToken)

            //create local user

            try {

                val userDto = response.userDto!!
                val userModel = userDto.toUserModel()

                val user = userRepository.findByEmail(userModel.email!!)

                if (user != null && user.email == user.email && user.uuid == userModel.uuid) { // assume that the user is the same
                    Logger.d("User already exists,will not create new user ,just setting as active")
                } else {
                    userRepository.insert(userModel)
                }


                //set kv data for user uuid

                userDataRepository.setUserUuid(userModel.uuid.toString())

                userDataRepository.setUserEmail(userModel.email)


            } catch (e: Exception) {
                Logger.e("Error creating User From Login Response")
                throw e
            }


        }




        return response
    }

}
