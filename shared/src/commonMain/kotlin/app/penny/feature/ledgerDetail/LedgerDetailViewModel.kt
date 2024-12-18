package app.penny.feature.ledgerDetail

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
    private val ledgerModel: LedgerModel,
    private val deleteLedgerUseCase: DeleteLedgerUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(LedgerDetailUiState(ledger = ledgerModel))
    val uiState: StateFlow<LedgerDetailUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: LedgerDetailIntent) {
        when (intent) {
            is LedgerDetailIntent.LoadLedger -> loadLedger()
            is LedgerDetailIntent.DeleteLedger -> deleteLedger()
        }
    }


    private fun loadLedger() {
        _uiState.value = _uiState.value.copy(isLoading = true)
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun deleteLedger() {
        screenModelScope.launch {
            try {
                deleteLedgerUseCase(
                    ledger = ledgerModel
                )
            } catch (e: IllegalStateException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                Logger.d { "Cannot delete the only ledger" }
            }
        }
    }


}