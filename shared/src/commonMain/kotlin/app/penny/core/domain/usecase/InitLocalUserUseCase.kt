package app.penny.core.domain.usecase

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.UserModel
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class InitLocalUserUseCase(
    private val userRepository: UserRepository,
    private val userDataRepository: UserDataRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke() {

        if (userRepository.findByEmailIsNull() != null) {
            Logger.d("Local User initialize skipped because local user already exists")
            return
        }

        val count  = userRepository.count()
        Logger.d("Local User count: $count")


        val userUuid = Uuid.random()

        userRepository.insert(
            UserModel(
                uuid = userUuid,
                username = "PennyPal",
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )


        userDataRepository.setUserUuid(userUuid.toString())

        Logger.d("Local User initialized")

    }
}