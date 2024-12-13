package app.penny.feature.profile

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ProfileUiState @OptIn(ExperimentalUuidApi::class) constructor(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val username: String? = null,
    val email: String? = null,
    val profileImage: String = "",
    val userUuid: Uuid = Uuid.fromLongs(0, 0),
    val modalInLoginMode : Boolean = true,
    val isLoggedIn: Boolean = false,
    val isEmailRegistered: Boolean = false,
    val loggingModalVisible: Boolean = false,

    val continuousCheckInDays: Int = 0,
    val totalTransactionCount: Int = 0,
    val totalTransactionDays: Int = 0,
)