// file: src/commonMain/kotlin/app/penny/feature/ledgerDetail/LedgerDetailViewModel.kt
package app.penny.feature.ledgerDetail

import app.penny.core.data.repository.StatisticRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.usecase.DeleteLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class LedgerDetailViewModel(
    private val ledger: LedgerModel,
    private val deleteLedgerUseCase: DeleteLedgerUseCase,
    private val statisticRepository: StatisticRepository,
) : ScreenModel {

    private val _uiState = MutableStateFlow(LedgerDetailUiState(ledger = ledger))
    val uiState: StateFlow<LedgerDetailUiState> = _uiState.asStateFlow()

    init {
        loadLedger()
    }

    fun handleIntent(intent: LedgerDetailIntent) {
        when (intent) {
            is LedgerDetailIntent.DeleteLedger -> deleteLedger()
            is LedgerDetailIntent.LoadLedger -> loadLedger()
            is LedgerDetailIntent.ChangeName -> changeName(intent.name)
        }
    }

    private fun loadLedger() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val summary = statisticRepository.getSummary(
                listOf(ledger)
            )


            try {
                _uiState.value = _uiState.value.copy(
                    ledger = ledger,
                    totalIncome = summary.totalIncome,
                    totalExpense = summary.totalExpense,
                    balance = summary.totalBalance,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message,
                    isLoading = false
                )
                Logger.e(e) { "Error loading ledger" }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun changeName(newName: String) {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(
                ledger = _uiState.value.ledger.copy(name = newName)
            )
        }
    }

    private fun deleteLedger() {
        screenModelScope.launch {
            try {
                deleteLedgerUseCase(ledger)
                // 处理删除后的导航或状态更新
            } catch (e: IllegalStateException) {
                _uiState.value = _uiState.value.copy(errorMessage = "Cannot delete the only ledger")
                Logger.d { "Cannot delete the only ledger" }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
                Logger.e(e) { "Error deleting ledger" }
            }
        }
    }
}