package app.penny.presentation.ui.screens.analytics

import app.penny.data.repository.UserDataRepository
import app.penny.domain.enum.TransactionType
import app.penny.domain.model.LedgerModel
import app.penny.domain.model.TransactionModel
import app.penny.domain.usecase.GetAllLedgerUseCase
import app.penny.domain.usecase.GetTransactionsByLedgerUseCase
import app.penny.utils.generateDateSequence
import app.penny.utils.getDaysInMonth
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.daysUntil
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


class AnalyticViewModel(
    private val getTransactionsByLedgerUseCase: GetTransactionsByLedgerUseCase,
    private val userDataRepository: UserDataRepository,
    private val getAllLedgerUseCase: GetAllLedgerUseCase
) : ScreenModel {
    private val _uiState = MutableStateFlow(AnalyticUiState())
    val uiState: StateFlow<AnalyticUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            fetchAllLedgers()
            _uiState.value = _uiState.value.copy(isLoading = false)
            Logger.d("ledgers: ${_uiState.value.ledgers}")
            val recentLedgerId = userDataRepository.getRecentLedgerId()

            if (recentLedgerId != -1L) {
                val recentLedger = _uiState.value.ledgers.find { it.id == recentLedgerId }
                Logger.d("get recentLedger $recentLedger")
                selectLedger(recentLedger!!)
                filterAndProcessTransactions("recent", ProcessStrategy.RECENT)

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
                filterAndProcessTransactions(
                    "recent/month/year/custom",
                    strategy = when (intent.tab) {
                        AnalyticTab.Recent -> ProcessStrategy.RECENT
                        AnalyticTab.Monthly -> ProcessStrategy.MONTHLY
                        AnalyticTab.Yearly -> ProcessStrategy.YEARLY
                        AnalyticTab.Custom -> ProcessStrategy.CUSTOM
                    }
                )
            }

            is AnalyticIntent.OnYearSelected -> {
                _uiState.value = _uiState.value.copy(selectedYear = intent.year)
                updateTimeFilterForYear(intent.year)
                filterAndProcessTransactions(
                    type = _uiState.value.selectedLedger.toString(),
                    strategy = ProcessStrategy.YEARLY
                )
            }

            is AnalyticIntent.OnYearMonthSelected -> {
                Logger.d("selected YearMonth${intent.yearMonth}")
                _uiState.value = _uiState.value.copy(selectedYearMonth = intent.yearMonth)
                updateTimeFilterForYearMonth(intent.yearMonth)
                filterAndProcessTransactions(
                    type = _uiState.value.selectedYearMonth.toString(),
                    strategy = ProcessStrategy.MONTHLY
                )
            }

            is AnalyticIntent.OnStartDateSelected -> {
                _uiState.value = _uiState.value.copy(startDate = intent.date)
                updateTimeFilterForCustomDate()
                filterAndProcessTransactions(
                    type = "${_uiState.value.startDate}- ${_uiState.value.endDate}",
                    strategy = ProcessStrategy.CUSTOM
                )
            }

            is AnalyticIntent.OnEndDateSelected -> {
                _uiState.value = _uiState.value.copy(endDate = intent.date)
                updateTimeFilterForCustomDate()
                filterAndProcessTransactions(
                    type = "${_uiState.value.startDate}- ${_uiState.value.endDate}",
                    strategy = ProcessStrategy.CUSTOM
                )
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
            }
        }
    }

    private suspend fun fetchAllLedgers() {
        val ledgers = getAllLedgerUseCase()
        _uiState.value = _uiState.value.copy(ledgers = ledgers)
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


    private fun filterAndProcessTransactions(type: String, strategy: ProcessStrategy) {
        val startInstant = _uiState.value.timeFilter.start
        val endInstant = _uiState.value.timeFilter.end
        val filtered = _uiState.value.allTransactions.filter {
            it.transactionDate in startInstant..endInstant
        }
        _uiState.value = _uiState.value.copy(filteredTransactions = filtered)
        processChartData(filtered, strategy)
        Logger.d("filtered ${_uiState.value.filteredTransactions.size} records in $type of ${_uiState.value.allTransactions.size}")
    }

    private fun processChartData(transactions: List<TransactionModel>, strategy: ProcessStrategy) {


        val timeZone = TimeZone.currentSystemDefault()
        if (transactions.isEmpty()) {
            // 如果没有交易数据，设置为空的图表数据
            _uiState.value = _uiState.value.copy(
                incomeExpenseTrendChartData = IncomeExpenseTrendChartData.empty
            )
            return
        }

        fun mappingDayOfMonth(localDate: LocalDate): LocalDate {
            //12 ->1 ,34->3
            val day = localDate.dayOfMonth
            return LocalDate(localDate.year, localDate.monthNumber, day.let {
                if (it % 2 == 0) it - 1 else it
            })
        }

        var localTimeMappingFunction: (LocalDate) -> String = {
            it.dayOfMonth.toString()
        }

        var xAxisLabels = emptyList<String>()
        var incomeValues = emptyList<Double>()
        var expenseValues = emptyList<Double>()

        when (strategy) {
            ProcessStrategy.MONTHLY -> {

                localTimeMappingFunction = { localDate ->
                    "${localDate.dayOfMonth}"
                }

                // 将数据按每两天一组进行分组
                val yearMonth = _uiState.value.selectedYearMonth
                val daysInMonth = getDaysInMonth(yearMonth.year, yearMonth.month)
                val localDateSequence = (1..daysInMonth step 2).map { day ->
                    LocalDate(yearMonth.year, yearMonth.month, day)
                }

                // 初始化收入和支出映射
                val incomeByDate = mutableMapOf<LocalDate, Double>()
                val expenseByDate = mutableMapOf<LocalDate, Double>()
                localDateSequence.forEach { date ->
                    incomeByDate[date] = 0.0
                    expenseByDate[date] = 0.0
                }

                // 将交易按映射后的日期分组
                val groupedTransactions = transactions.groupBy {
                    mappingDayOfMonth(it.transactionDate.toLocalDateTime(timeZone).date)
                }

                // 汇总收入和支出
                groupedTransactions.forEach { (date, transactionsInGroup) ->
                    transactionsInGroup.forEach { transaction ->
                        val amount = transaction.amount.toPlainString().toDouble()
                        when (transaction.transactionType) {
                            TransactionType.INCOME -> {
                                incomeByDate[date] = (incomeByDate[date] ?: 0.0) + amount
                            }

                            TransactionType.EXPENSE -> {
                                expenseByDate[date] = (expenseByDate[date] ?: 0.0) + amount
                            }

                            else -> {}
                        }
                    }
                }

                // 准备图表数据
                xAxisLabels = localDateSequence.map { localTimeMappingFunction(it) }
                incomeValues = localDateSequence.map { incomeByDate[it] ?: 0.0 }
                expenseValues = localDateSequence.map { expenseByDate[it] ?: 0.0 }


            }

            ProcessStrategy.RECENT -> {
                // 最近7天，按天分组
                val startDate = _uiState.value.timeFilter.start.toLocalDateTime(timeZone).date
                val endDate = _uiState.value.timeFilter.end.toLocalDateTime(timeZone).date
                val dateSequence = generateDateSequence(startDate, endDate)

                // 初始化收入和支出映射
                val incomeByDate = mutableMapOf<LocalDate, Double>()
                val expenseByDate = mutableMapOf<LocalDate, Double>()
                dateSequence.forEach { date ->
                    incomeByDate[date] = 0.0
                    expenseByDate[date] = 0.0
                }

                // 将交易按日期分组
                val groupedTransactions = transactions.groupBy {
                    it.transactionDate.toLocalDateTime(timeZone).date
                }

                // 汇总收入和支出
                groupedTransactions.forEach { (date, transactionsInGroup) ->
                    transactionsInGroup.forEach { transaction ->
                        val amount = transaction.amount.toPlainString().toDouble()
                        when (transaction.transactionType) {
                            TransactionType.INCOME -> {
                                incomeByDate[date] = (incomeByDate[date] ?: 0.0) + amount
                            }

                            TransactionType.EXPENSE -> {
                                expenseByDate[date] = (expenseByDate[date] ?: 0.0) + amount
                            }

                            else -> {}
                        }
                    }
                }

                // 准备图表数据
                xAxisLabels = dateSequence.map { "${it.monthNumber}-${it.dayOfMonth}" }
                incomeValues = dateSequence.map { incomeByDate[it] ?: 0.0 }
                expenseValues = dateSequence.map { expenseByDate[it] ?: 0.0 }
            }


            ProcessStrategy.YEARLY -> {
                // 按月份分组
                val selectedYear = _uiState.value.selectedYear
                val monthsInYear = (1..12).toList()

                // 初始化收入和支出映射
                val incomeByMonth = mutableMapOf<Int, Double>()
                val expenseByMonth = mutableMapOf<Int, Double>()
                monthsInYear.forEach { month ->
                    incomeByMonth[month] = 0.0
                    expenseByMonth[month] = 0.0
                }

                // 将交易按月份分组
                val groupedTransactions = transactions.groupBy {
                    it.transactionDate.toLocalDateTime(timeZone).date.monthNumber
                }

                // 汇总收入和支出
                groupedTransactions.forEach { (month, transactionsInGroup) ->
                    transactionsInGroup.forEach { transaction ->
                        val amount = transaction.amount.toPlainString().toDouble()
                        when (transaction.transactionType) {
                            TransactionType.INCOME -> {
                                incomeByMonth[month] = (incomeByMonth[month] ?: 0.0) + amount
                            }

                            TransactionType.EXPENSE -> {
                                expenseByMonth[month] = (expenseByMonth[month] ?: 0.0) + amount
                            }

                            else -> {}
                        }
                    }
                }

                // 准备图表数据
                xAxisLabels = monthsInYear.map {
                    when (it) {
                        1 -> "Jan"
                        2 -> "Feb"
                        3 -> "Mar"
                        4 -> "Apr"
                        5 -> "May"
                        6 -> "Jun"
                        7 -> "Jul"
                        8 -> "Aug"
                        9 -> "Sep"
                        10 -> "Oct"
                        11 -> "Nov"
                        12 -> "Dec"
                        else -> ""
                    }
                }
                incomeValues = monthsInYear.map { incomeByMonth[it] ?: 0.0 }
                expenseValues = monthsInYear.map { expenseByMonth[it] ?: 0.0 }
            }

            ProcessStrategy.CUSTOM -> {


            }
        }


        // 更新 UI 状态
        _uiState.value = _uiState.value.copy(
            incomeExpenseTrendChartData = IncomeExpenseTrendChartData(
                xAxisData = xAxisLabels,
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
                val startDate = LocalDate(yearMonth.year, yearMonth.month, 1)
                val endDate = startDate.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
                start = startDate.atStartOfDayIn(timeZone)
                end = endDate.atStartOfDayIn(timeZone)
            }

            AnalyticTab.Yearly -> {
                val year = _uiState.value.selectedYear
                val startDate = LocalDate(year, 1, 1)
                val endDate = LocalDate(year, 12, 31)
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
        val startDate = LocalDate(year, 1, 1)
        val endDate = LocalDate(year, 12, 31)
        val start = startDate.atStartOfDayIn(timeZone)
        val end = endDate.atStartOfDayIn(timeZone)
        _uiState.value = _uiState.value.copy(timeFilter = TimeFilter(start, end))
    }

    private fun updateTimeFilterForYearMonth(yearMonth: YearMonth) {
        val timeZone = TimeZone.currentSystemDefault()
        val startDate = LocalDate(yearMonth.year, yearMonth.month, 1)
        val endDate = startDate.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
        val start = startDate.atStartOfDayIn(timeZone)
        val end = endDate.atStartOfDayIn(timeZone).plus(23.hours).plus(59.minutes).plus(59.seconds)
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


enum class ProcessStrategy {
    RECENT,
    MONTHLY,
    YEARLY,
    CUSTOM
}