package app.penny.feature.dashboard

import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.usecase.DownloadUnsyncedLedgerUseCase
import app.penny.core.domain.usecase.InsertRandomTransactionUseCase
import app.penny.core.domain.usecase.SyncDataUseCase
import app.penny.core.domain.usecase.UploadUnsyncedLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi


class DashboardViewModel(
    private val insertRandomTransactionUseCase: InsertRandomTransactionUseCase,
    private val uploadUnsyncedLedgerUseCase: UploadUnsyncedLedgerUseCase,
    private val userDataRepository: UserDataRepository,
    private val downloadUnsyncedLedgerUseCase: DownloadUnsyncedLedgerUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        fetchUserData()
    }

    fun showAddTransactionModal() {
        _uiState.value = _uiState.value.copy(addTransactionModalVisible = true)
    }

    fun hideAddTransactionModal() {
        _uiState.value = _uiState.value.copy(addTransactionModalVisible = false)
    }


    private fun insertTransaction() {
        _uiState.value = DashboardUiState(isLoading = true)
    }


    fun handleIntent(intent: DashboardIntent) {
        when (intent) {

            is DashboardIntent.UploadUpdatedLedgers ->
                uploadUpdatedLedgers()

            is DashboardIntent.ClearUserData ->
                clearUserData()


            is DashboardIntent.DownloadUnsyncedLedgers ->
                downloadUnsyncedLedgers()

            else -> {
            }
        }
    }

    fun downloadUnsyncedLedgers() {
        screenModelScope.launch {
            downloadUnsyncedLedgerUseCase()
        }
        Logger.d("downloaded ledgers")
    }

    fun insertRandomTransaction(count: Int, tier: Int) {
        screenModelScope.launch {
            insertRandomTransactionUseCase(
                count = count,
                tier = tier
            )
        }
        Logger.d("inserted Random Transactions ,count $count")

    }

    fun uploadUpdatedLedgers() {
        screenModelScope.launch {
            uploadUnsyncedLedgerUseCase()
        }
        Logger.d("uploaded updated ledgers")
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun fetchUserData() {
        screenModelScope.launch {
            val lastSyncedAt = userDataRepository.getLastSyncedAt()
            _uiState.value = _uiState.value.copy(lastSyncedAt = lastSyncedAt)

            val userUuid = userDataRepository.getUserUuid()
            val user = userRepository.findByUuid(userUuid)
            _uiState.value = _uiState.value.copy(activeUser = user)

        }
    }

    fun clearUserData() {
        screenModelScope.launch {
            userDataRepository.clearUserData()
            authRepository.clearToken()
        }
        Logger.d("cleared user data")
    }

    fun syncAllData() {
        screenModelScope.launch {
            syncDataUseCase()
        }

        Logger.d("synced all data")
        _uiState.value = _uiState.value.copy(
            message = "Synced all data"
        )
    }

}


