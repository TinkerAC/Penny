package app.penny.feature.profile

import app.penny.core.domain.model.UserModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ProfileUiState @OptIn(ExperimentalUuidApi::class) constructor(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val username: String? = null,
    val email: String? = null,
    val profileImage: String = "",
    val user:UserModel? = null,
    val modalInLoginMode: Boolean = true,
    val isLoggedIn: Boolean = false,
    val isEmailRegistered: Boolean = false,
    val loggingModalVisible: Boolean = false,
    val ledgerCount: Long = 0,
    val totalTransactionCount: Long = 0,
    val totalTransactionDateSpan: Long = 0,
)