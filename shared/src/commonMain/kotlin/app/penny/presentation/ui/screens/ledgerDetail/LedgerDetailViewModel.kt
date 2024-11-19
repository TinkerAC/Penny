package app.penny.presentation.ui.screens.ledgerDetail

import app.penny.domain.model.LedgerModel
import app.penny.domain.usecase.DeleteLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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

    private fun deleteLedger() {
        screenModelScope.launch {
            deleteLedgerUseCase(ledgerId = ledgerModel.id)
        }
    }


}