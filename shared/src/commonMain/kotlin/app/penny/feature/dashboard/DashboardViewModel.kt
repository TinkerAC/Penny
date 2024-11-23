package app.penny.feature.dashboard

import app.penny.core.domain.usecase.InsertLedgerUseCase
import app.penny.core.domain.usecase.InsertRandomTransactionUseCase
import app.penny.core.domain.usecase.UploadUpdatedLedgersUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DashboardViewModel(
    private val insertLedgerUseCase: InsertLedgerUseCase,
    private val insertRandomTransactionUseCase: InsertRandomTransactionUseCase,
    private val uploadUpdatedLedgersUseCase: UploadUpdatedLedgersUseCase
) : ScreenModel {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()


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
            is DashboardIntent.InsertRandomTransaction ->
                insertRandomTransaction()

            is DashboardIntent.UploadUpdatedLedgers ->
                uploadUpdatedLedgers()
        }
    }

    private fun insertRandomTransaction(count: Int = 100) {
        screenModelScope.launch {
            insertRandomTransactionUseCase(100)
        }
        Logger.d("inserted Random Transactions ,count $count")

    }

    private fun uploadUpdatedLedgers() {
        screenModelScope.launch {
            uploadUpdatedLedgersUseCase()
        }
        Logger.d("uploaded updated ledgers")
    }
}


