package app.penny.presentation.ui.screens.analytics

import app.penny.data.repository.UserDataRepository
import app.penny.domain.model.LedgerModel
import app.penny.domain.usecase.GetAllLedgerUseCase
import app.penny.domain.usecase.GetTransactionsByLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyticViewModel(
    private val getTransactionsByLedgerUseCase: GetTransactionsByLedgerUseCase,
    private val userDataRepository: UserDataRepository,
    private val getAllLedgerUseCase: GetAllLedgerUseCase
) : ScreenModel {
    private val _uiState = MutableStateFlow(AnalyticUiState())
    val uiState: StateFlow<AnalyticUiState> = _uiState.asStateFlow()


    init {
        screenModelScope.launch {
            fetchLedgers()
            fetchTransactions()
            val recentLedgerId = userDataRepository.getRecentLedgerId()
            uiState.value.ledgers.find { it.id == recentLedgerId }?.let {
                _uiState.value = _uiState.value.copy(selectedLedger = it)
            }
        }

    }

    fun handleIntent(intent: AnalyticIntent) {
        when (intent) {
            is AnalyticIntent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = intent.tab)
            }


            is AnalyticIntent.OnYearSelected -> {
                _uiState.value = _uiState.value.copy(selectedYear = intent.year)
            }

            is AnalyticIntent.OnYearMonthSelected -> {
                _uiState.value = _uiState.value.copy(selectedYearMonth = intent.yearMonth)
            }

            is AnalyticIntent.OnStartDateSelected -> {
                _uiState.value = _uiState.value.copy(startDate = intent.date)
            }

            is AnalyticIntent.OnEndDateSelected -> {
                _uiState.value = _uiState.value.copy(endDate = intent.date)
            }

            is AnalyticIntent.ShowLedgerSelectionDialog -> {
                showLedgerSelectionDialog()
            }

            is AnalyticIntent.DismissLedgerSelectionDialog -> {
                dismissLedgerSelectionDialog()
            }

            is AnalyticIntent.SelectLedger -> selectLedger(ledger = intent.ledger)
        }
    }

    private fun showLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = true)
    }

    private fun dismissLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = false)
    }


    private fun fetchTransactions() {
        // 从数据库中获取数据
        screenModelScope.launch {
            uiState.value.selectedLedger?.let { ledger ->
                val transactions = getTransactionsByLedgerUseCase(ledger.id)
                _uiState.value = _uiState.value.copy(transactions = transactions)
            }
        }
    }

    private fun fetchLedgers() {
        screenModelScope.launch {
            val ledgers = getAllLedgerUseCase()
            _uiState.value = _uiState.value.copy(ledgers = ledgers)
        }
    }

    private fun selectLedger(ledger: LedgerModel) {

        Logger.d("Now Using Ledger: $ledger")

        _uiState.value = _uiState.value.copy(selectedLedger = ledger)

        screenModelScope.launch {
            userDataRepository.saveRecentLedgerId(ledger.id)
            fetchTransactions()
        }

    }


}

