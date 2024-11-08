package app.penny.presentation.ui.screens.analytics

import app.penny.presentation.ui.screens.newTransaction.NewTransactionUiState
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnalyticViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(AnalyticUiState())
    val uiState: StateFlow<AnalyticUiState> = _uiState.asStateFlow()


    fun handleIntent(intent: AnalyticIntent) {
        when (intent) {
            is AnalyticIntent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = intent.tab)
            }
            is AnalyticIntent.OnLedgerIconClicked -> {
                // 处理Ledger切换逻辑
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
            else -> {
                // 对于Recent选项，无需额外操作
            }
        }
    }

}
