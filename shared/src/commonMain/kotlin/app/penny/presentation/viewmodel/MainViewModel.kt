package app.penny.presentation.viewmodel

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.LedgerCover
import app.penny.core.domain.usecase.InsertLedgerUseCase
import app.penny.presentation.uiState.MainUiState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val ledgerRepository: LedgerRepository,
    private val insertLedgerUseCase: InsertLedgerUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()


    fun fetchLedgers() {
        // 通过协程获取数据
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(ledgers = ledgerRepository.findAll())
        }
    }


    fun insertLedger(name: String, currency: Currency, description: String, cover: LedgerCover) {
        screenModelScope.launch {
            insertLedgerUseCase(
                name = name,
                currency = currency,
                cover = cover,
                description = description
            )

        }
    }


}