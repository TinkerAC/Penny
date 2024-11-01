package app.penny.presentation.viewmodel

import app.penny.domain.enum.Currency
import app.penny.domain.enum.LedgerCover
import app.penny.domain.usecase.InsertLedgerUseCase
import app.penny.presentation.NewLedgerUiState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewLedgerViewModel(
    private val insertLedgerUseCase: InsertLedgerUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(NewLedgerUiState())
    val uiState: StateFlow<NewLedgerUiState> = _uiState.asStateFlow()


    fun createNewLedger(
        name: String,
        currencyCode: String,
        cover: LedgerCover,
        description: String
    ) {

        screenModelScope.launch {
            insertLedgerUseCase(
                name = name,
                currencyCode = currencyCode,
                cover = cover,
                description = description
            )
        }

    }


    fun setCover(cover: LedgerCover) {
        _uiState.value = _uiState.value.copy(ledgerCover = cover)
    }

    fun setName(name: String) {
        _uiState.value = _uiState.value.copy(ledgerName = name)
    }

    fun setDescription(description: String) {
        _uiState.value = _uiState.value.copy(ledgerDescription = description)
    }

    fun setCurrencyCode(currencyCode: String) {
        _uiState.value = _uiState.value.copy(ledgerCurrencyCode = currencyCode)
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setCurrency(currency: Currency) {
        _uiState.value = _uiState.value.copy(currency = currency)
    }

    fun setCurrencySelectorModalVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(currencySelectorModalVisible = visible)
    }


}