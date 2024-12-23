// file: src/commonMain/kotlin/app/penny/feature/ledgerDetail/LedgerDetailViewModel.kt
package app.penny.feature.ledgerDetail

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.StatisticRepository
import app.penny.core.domain.exception.LedgerException
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.usecase.DeleteLedgerUseCase
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class LedgerDetailViewModel(
    private val ledger: LedgerModel,
    private val deleteLedgerUseCase: DeleteLedgerUseCase,
    private val statisticRepository: StatisticRepository,
    private val ledgerRepository: LedgerRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(LedgerDetailUiState(ledger = ledger))
    val uiState: StateFlow<LedgerDetailUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LedgerDetailUiEvent>(replay = 0)
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadLedger()
    }

    fun handleIntent(intent: LedgerDetailIntent) {
        when (intent) {
            is LedgerDetailIntent.DeleteLedger -> deleteLedger()
            is LedgerDetailIntent.LoadLedger -> loadLedger()
            is LedgerDetailIntent.ChangeName -> changeName(intent.name)
            is LedgerDetailIntent.SaveLedger -> saveLedger()
        }
    }

    private fun saveLedger() {
        screenModelScope.launch {
            try {
                ledgerRepository.update(_uiState.value.ledger)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
                Logger.e(e) { "Error saving ledger" }
            }
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
                    totalIncome = summary.income,
                    totalExpense = summary.expense,
                    balance = summary.balance,
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
                deleteLedgerUseCase(_uiState.value.ledger)
                _eventFlow.emit(
                    LedgerDetailUiEvent.ShowSnackBar(
                        message = SharedRes.strings.ledger_deleted_successfully
                    )
                )
            } catch (e: LedgerException.CanNotDeleteDefaultLedger) {
                _eventFlow.emit(
                    LedgerDetailUiEvent.ShowSnackBar(
                        message = SharedRes.strings.can_not_delete_default_ledger
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
                Logger.e(e) { "Error deleting ledger" }
            }


        }
    }
}