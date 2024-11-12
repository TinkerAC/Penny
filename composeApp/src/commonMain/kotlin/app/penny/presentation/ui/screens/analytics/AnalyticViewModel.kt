package app.penny.presentation.ui.screens.analytics

import app.penny.data.repository.UserDataRepository
import app.penny.domain.model.LedgerModel
import app.penny.domain.model.TransactionModel
import app.penny.domain.usecase.GetAllLedgerUseCase
import app.penny.domain.usecase.GetTransactionsByLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class AnalyticViewModel(
    private val getTransactionsByLedgerUseCase: GetTransactionsByLedgerUseCase,
    private val userDataRepository: UserDataRepository,
    private val getAllLedgerUseCase: GetAllLedgerUseCase
) : ScreenModel {
    private val _uiState = MutableStateFlow(AnalyticUiState())
    val uiState: StateFlow<AnalyticUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            fetchAllLedgers()
            val recentLedgerId = userDataRepository.getRecentLedgerId()
            Logger.d("get recentLedgerId $recentLedgerId")
            if (recentLedgerId != -1L) {
                _uiState.value.ledgers.find { it.id == recentLedgerId }?.let { ledger ->
                    selectLedger(ledger)
                }
            } else {
                // 如果没有最近使用的账本，显示账本选择对话框
                showLedgerSelectionDialog()
            }
        }
    }

    fun handleIntent(intent: AnalyticIntent) {
        when (intent) {
            is AnalyticIntent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = intent.tab)
                updateTimeFilterBasedOnTab(intent.tab)
                filterTransactions()
            }
            is AnalyticIntent.OnYearSelected -> {
                _uiState.value = _uiState.value.copy(selectedYear = intent.year)
                updateTimeFilterForYear(intent.year)
                filterTransactions()
            }
            is AnalyticIntent.OnYearMonthSelected -> {
                _uiState.value = _uiState.value.copy(selectedYearMonth = intent.yearMonth)
                updateTimeFilterForYearMonth(intent.yearMonth)
                filterTransactions()
            }
            is AnalyticIntent.OnStartDateSelected -> {
                _uiState.value = _uiState.value.copy(startDate = intent.date)
                updateTimeFilterForCustomDate()
                filterTransactions()
            }
            is AnalyticIntent.OnEndDateSelected -> {
                _uiState.value = _uiState.value.copy(endDate = intent.date)
                updateTimeFilterForCustomDate()
                filterTransactions()
            }
            AnalyticIntent.ShowLedgerSelectionDialog -> {
                showLedgerSelectionDialog()
            }
            AnalyticIntent.DismissLedgerSelectionDialog -> {
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

    private fun fetchTransactionsOfSelectedLedger() {
        // 从数据库中获取数据
        screenModelScope.launch {
            uiState.value.selectedLedger?.let { ledger ->
                val transactions = getTransactionsByLedgerUseCase(ledger.id)
                _uiState.value = _uiState.value.copy(allTransactions = transactions)
                filterTransactions()
            }
        }
    }

    private fun fetchAllLedgers() {
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
            Logger.d("Save recentLedgerId ${ledger.id}")
            dismissLedgerSelectionDialog()
            fetchTransactionsOfSelectedLedger()
        }
    }

    private fun filterTransactions() {
        val startInstant = _uiState.value.timeFilter.start
        val endInstant = _uiState.value.timeFilter.end
        val filtered = _uiState.value.allTransactions.filter {
            it.transactionDate in startInstant..endInstant
        }
        _uiState.value = _uiState.value.copy(filteredTransactions = filtered)
        processChartData(filtered)
    }

    private fun processChartData(transactions: List<TransactionModel>) {
        // 处理图表数据
        val timeZone = TimeZone.currentSystemDefault()

        // 按日期汇总收入和支出
        val incomeByDate = mutableMapOf<String, Double>()
        val expenseByDate = mutableMapOf<String, Double>()

        transactions.forEach { transaction ->
            val dateStr = transaction.transactionDate.toLocalDateTime(timeZone).date.toString()
            val amount = transaction.amount.toPlainString().toDouble()

            when (transaction.transactionType) {
                app.penny.domain.enum.TransactionType.INCOME -> {
                    incomeByDate[dateStr] = (incomeByDate[dateStr] ?: 0.0) + amount
                }
                app.penny.domain.enum.TransactionType.EXPENSE -> {
                    expenseByDate[dateStr] = (expenseByDate[dateStr] ?: 0.0) + amount
                }
                else -> {}
            }
        }

        // 获取所有日期并排序
        val dates = (incomeByDate.keys + expenseByDate.keys).distinct().sorted()

        // 准备收入和支出数据
        val incomeValues = dates.map { incomeByDate[it] ?: 0.0 }
        val expenseValues = dates.map { expenseByDate[it] ?: 0.0 }

        // 更新 UI 状态
        _uiState.value = _uiState.value.copy(
            chartData = ChartData(
                xAxisData = dates,
                incomeValues = incomeValues,
                expenseValues = expenseValues
            )
        )
    }

    private fun updateTimeFilterBasedOnTab(tab: AnalyticTab) {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now().toLocalDateTime(timeZone)
        val start: Instant
        val end: Instant

        when (tab) {
            AnalyticTab.Recent -> {
                start = now.date.minus(DatePeriod(days = 7)).atStartOfDayIn(timeZone)
                end = now.date.atStartOfDayIn(timeZone)
            }
            AnalyticTab.Monthly -> {
                val yearMonth = _uiState.value.selectedYearMonth
                val startDate = kotlinx.datetime.LocalDate(yearMonth.year, yearMonth.month, 1)
                val endDate = startDate.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
                start = startDate.atStartOfDayIn(timeZone)
                end = endDate.atStartOfDayIn(timeZone)
            }
            AnalyticTab.Yearly -> {
                val year = _uiState.value.selectedYear
                val startDate = kotlinx.datetime.LocalDate(year, 1, 1)
                val endDate = kotlinx.datetime.LocalDate(year, 12, 31)
                start = startDate.atStartOfDayIn(timeZone)
                end = endDate.atStartOfDayIn(timeZone)
            }
            AnalyticTab.Custom -> {
                val startDate = _uiState.value.startDate
                val endDate = _uiState.value.endDate
                start = startDate.atStartOfDayIn(timeZone)
                end = endDate.atStartOfDayIn(timeZone)
            }
        }

        _uiState.value = _uiState.value.copy(timeFilter = TimeFilter(start, end))
    }

    private fun updateTimeFilterForYear(year: Int) {
        val timeZone = TimeZone.currentSystemDefault()
        val startDate = kotlinx.datetime.LocalDate(year, 1, 1)
        val endDate = kotlinx.datetime.LocalDate(year, 12, 31)
        val start = startDate.atStartOfDayIn(timeZone)
        val end = endDate.atStartOfDayIn(timeZone)
        _uiState.value = _uiState.value.copy(timeFilter = TimeFilter(start, end))
    }

    private fun updateTimeFilterForYearMonth(yearMonth: YearMonth) {
        val timeZone = TimeZone.currentSystemDefault()
        val startDate = kotlinx.datetime.LocalDate(yearMonth.year, yearMonth.month, 1)
        val endDate = startDate.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
        val start = startDate.atStartOfDayIn(timeZone)
        val end = endDate.atStartOfDayIn(timeZone)
        _uiState.value = _uiState.value.copy(timeFilter = TimeFilter(start, end))
    }

    private fun updateTimeFilterForCustomDate() {
        val timeZone = TimeZone.currentSystemDefault()
        val startDate = _uiState.value.startDate
        val endDate = _uiState.value.endDate
        val start = startDate.atStartOfDayIn(timeZone)
        val end = endDate.atStartOfDayIn(timeZone)
        _uiState.value = _uiState.value.copy(timeFilter = TimeFilter(start, end))
    }
}