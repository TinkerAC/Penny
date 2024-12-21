package app.penny.feature.analytics

import androidx.compose.ui.graphics.Color
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.domain.usecase.SearchTransactionsUseCase
import app.penny.core.utils.generateDateSequence
import app.penny.core.utils.getDaysInMonth
import app.penny.core.utils.localDateNow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.aay.compose.donutChart.model.PieChartData
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AnalyticViewModel(
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository,
    private val searchTransactionsUseCase: SearchTransactionsUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(AnalyticUiState())
    val uiState: StateFlow<AnalyticUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadUserLedgers()
            val recentLedger = userDataRepository.getDefaultLedger()
            selectLedger(recentLedger)
            _uiState.value = _uiState.value.copy(isLoading = false)
            selectTab(AnalyticTab.Recent)
        }
    }

    fun handleIntent(intent: AnalyticIntent) {
        when (intent) {
            is AnalyticIntent.OnTabSelected -> selectTab(intent.tab)
            is AnalyticIntent.OnYearSelected -> selectYear(intent.year)
            is AnalyticIntent.OnYearMonthSelected -> selectYearMonth(intent.yearMonth)
            is AnalyticIntent.OnStartDateSelected -> updateDateRange(startDate = intent.date)
            is AnalyticIntent.OnEndDateSelected -> updateDateRange(endDate = intent.date)
            AnalyticIntent.ShowLedgerSelectionDialog -> showLedgerSelectionDialog()
            AnalyticIntent.DismissLedgerSelectionDialog -> dismissLedgerSelectionDialog()
            is AnalyticIntent.SelectLedger -> selectLedger(intent.ledger)

        }
    }

    private suspend fun loadUserLedgers() {
        val user = userDataRepository.getUser()
        val ledgers = ledgerRepository.findByUserUuid(user.uuid)
        Logger.d("User $user has ledgers: $ledgers")
        _uiState.value = _uiState.value.copy(ledgers = ledgers)
    }

    private fun selectLedger(ledger: LedgerModel) {
        screenModelScope.launch {
            Logger.d("Selected Ledger: ${ledger.uuid}")
            _uiState.value = _uiState.value.copy(selectedLedger = ledger)
            dismissLedgerSelectionDialog()
            selectTab(_uiState.value.selectedTab)
        }
    }

    private fun selectTab(tab: AnalyticTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        when (tab) {
            AnalyticTab.Recent -> setDateRangeForRecent()
            AnalyticTab.Monthly -> setDateRangeForMonthly()
            AnalyticTab.Yearly -> setDateRangeForYearly()
            AnalyticTab.Custom -> { /* 自定义时间范围由用户选择 */
            }
        }
        performFilter()
    }

    private fun setDateRangeForRecent() {
        val endDate = localDateNow()
        val startDate = endDate.minus(DatePeriod(days = 7))
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
    }

    private fun setDateRangeForMonthly() {
        val yearMonth = _uiState.value.selectedYearMonth
        val startDate = LocalDate(yearMonth.year, yearMonth.month, 1)
        val endDate = LocalDate(yearMonth.year, yearMonth.month, getDaysInMonth(yearMonth))
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
    }

    private fun setDateRangeForYearly() {
        val year = _uiState.value.selectedYear
        val startDate = LocalDate(year, 1, 1)
        val endDate = LocalDate(year, 12, 31)
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
    }

    private fun selectYear(year: Int) {
        _uiState.value = _uiState.value.copy(selectedYear = year)
        setDateRangeForYearly()
        performFilter()
    }

    private fun selectYearMonth(yearMonth: YearMonth) {
        _uiState.value = _uiState.value.copy(selectedYearMonth = yearMonth)
        setDateRangeForMonthly()
        performFilter()
    }

    private fun updateDateRange(startDate: LocalDate? = null, endDate: LocalDate? = null) {
        _uiState.value = _uiState.value.copy(
            startDate = startDate ?: _uiState.value.startDate,
            endDate = endDate ?: _uiState.value.endDate
        )
        performFilter()
    }

    private fun showLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = true)
    }

    private fun dismissLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = false)
    }

    private fun performFilter() {
        val selectedLedger = _uiState.value.selectedLedger
        val startDate = _uiState.value.startDate
        val endDate = _uiState.value.endDate

        if (selectedLedger != null) {
            screenModelScope.launch {
                val transactions = searchTransactionsUseCase(
                    ledgerUuid = selectedLedger.uuid,
                    startDate = startDate,
                    endDate = endDate
                )
                _uiState.value = _uiState.value.copy(filteredTransactions = transactions)
                prepareDataForCharts(transactions)
            }
        } else {
            _uiState.value = _uiState.value.copy(filteredTransactions = emptyList())
            prepareDataForCharts(emptyList())
        }
    }

    private fun prepareDataForCharts(transactions: List<TransactionModel>) {
        val strategy = when (_uiState.value.selectedTab) {
            AnalyticTab.Recent -> ProcessStrategy.RECENT
            AnalyticTab.Monthly -> ProcessStrategy.MONTHLY
            AnalyticTab.Yearly -> ProcessStrategy.YEARLY
            AnalyticTab.Custom -> ProcessStrategy.CUSTOM
        }
        val trendData = ChartDataProcessor.processIncomeExpenseTrendLineChartData(
            transactions,
            strategy,
            _uiState.value
        )
        val (incomePieData, expensePieData) = ChartDataProcessor.processCategoryPieChartData(
            transactions
        )


        val assetTableData = ChartDataProcessor.processAssetTableData(transactions)
        val assetTrendData = ChartDataProcessor.processAssetTrendLineChartData(assetTableData)

        _uiState.value = _uiState.value.copy(
            incomeExpenseTrendChartData = trendData,
            incomePieChartData = incomePieData,
            expensePieChartData = expensePieData,
            assetChangeTableData = assetTableData,
            assetTrendLineChartData = assetTrendData
        )
    }
}

// 独立的图表数据处理类
object ChartDataProcessor {
    fun processIncomeExpenseTrendLineChartData(
        transactions: List<TransactionModel>,
        strategy: ProcessStrategy,
        uiState: AnalyticUiState
    ): IncomeExpenseTrendChartData {
        val timeZone = TimeZone.currentSystemDefault()
        if (transactions.isEmpty()) {
            return IncomeExpenseTrendChartData.empty
        }

        val (xAxisLabels, incomeValues, expenseValues) = when (strategy) {
            ProcessStrategy.MONTHLY -> processMonthlyStrategy(transactions, uiState, timeZone)
            ProcessStrategy.RECENT -> processRecentStrategy(transactions, timeZone)
            ProcessStrategy.YEARLY -> processYearlyStrategy(transactions, timeZone)
            ProcessStrategy.CUSTOM -> processCustomStrategy(transactions, uiState, timeZone)
        }

        return IncomeExpenseTrendChartData(
            xAxisData = xAxisLabels,
            incomeValues = incomeValues,
            expenseValues = expenseValues
        )
    }

    private fun processRecentStrategy(
        transactions: List<TransactionModel>,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        val startDate = localDateNow().minus(DatePeriod(days = 7))
        val endDate = localDateNow()
        val dateSequence = generateDateSequence(startDate, endDate)

        val incomeByDate = transactions
            .filter { it.transactionType == TransactionType.INCOME }
            .groupBy { it.transactionInstant.toLocalDateTime(timeZone).date }
            .mapValues { it.value.sumOf { txn -> txn.amount.toPlainString().toDouble() } }

        val expenseByDate = transactions
            .filter { it.transactionType == TransactionType.EXPENSE }
            .groupBy { it.transactionInstant.toLocalDateTime(timeZone).date }
            .mapValues { it.value.sumOf { txn -> txn.amount.toPlainString().toDouble() } }

        val xAxisLabels = dateSequence.map { "${it.monthNumber}-${it.dayOfMonth}" }
        val incomeValues = dateSequence.map { incomeByDate[it] ?: 0.0 }
        val expenseValues = dateSequence.map { expenseByDate[it] ?: 0.0 }

        return Triple(xAxisLabels, incomeValues, expenseValues)
    }

    private fun processMonthlyStrategy(
        transactions: List<TransactionModel>,
        uiState: AnalyticUiState,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        val yearMonth = uiState.selectedYearMonth
        val daysInMonth = getDaysInMonth(yearMonth)
        val localDateSequence = (1..daysInMonth step 2).map { day ->
            LocalDate(yearMonth.year, yearMonth.month, day)
        }

        val incomeByDate = localDateSequence.associateWith { 0.0 }.toMutableMap()
        val expenseByDate = localDateSequence.associateWith { 0.0 }.toMutableMap()

        transactions.forEach { txn ->
            val date = txn.transactionInstant.toLocalDateTime(timeZone).date
            val mappedDate = LocalDate(
                date.year,
                date.monthNumber,
                if (date.dayOfMonth % 2 == 0) date.dayOfMonth - 1 else date.dayOfMonth
            )
            if (mappedDate in incomeByDate) {
                if (txn.transactionType == TransactionType.INCOME) {
                    incomeByDate[mappedDate] =
                        incomeByDate[mappedDate]!! + txn.amount.toPlainString().toDouble()
                } else if (txn.transactionType == TransactionType.EXPENSE) {
                    expenseByDate[mappedDate] =
                        expenseByDate[mappedDate]!! + txn.amount.toPlainString().toDouble()
                }
            }
        }

        val xAxisLabels = localDateSequence.map { it.dayOfMonth.toString() }
        val incomeValues = localDateSequence.map { incomeByDate[it] ?: 0.0 }
        val expenseValues = localDateSequence.map { expenseByDate[it] ?: 0.0 }

        return Triple(xAxisLabels, incomeValues, expenseValues)
    }

    private fun processYearlyStrategy(
        transactions: List<TransactionModel>,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        val selectedYear = localDateNow().year // 假设需要从uiState获取
        val monthsInYear = (1..12).toList()

        val incomeByMonth = monthsInYear.associateWith { 0.0 }.toMutableMap()
        val expenseByMonth = monthsInYear.associateWith { 0.0 }.toMutableMap()

        transactions.forEach { txn ->
            val date = txn.transactionInstant.toLocalDateTime(timeZone).date
            if (date.year == selectedYear) {
                val month = date.monthNumber
                if (txn.transactionType == TransactionType.INCOME) {
                    incomeByMonth[month] =
                        incomeByMonth[month]!! + txn.amount.toPlainString().toDouble()
                } else if (txn.transactionType == TransactionType.EXPENSE) {
                    expenseByMonth[month] =
                        expenseByMonth[month]!! + txn.amount.toPlainString().toDouble()
                }
            }
        }
        val monthLabels = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )


        val xAxisLabels = monthsInYear.map { monthLabels[it - 1] }
        val incomeValues = monthsInYear.map { incomeByMonth[it] ?: 0.0 }
        val expenseValues = monthsInYear.map { expenseByMonth[it] ?: 0.0 }

        return Triple(xAxisLabels, incomeValues, expenseValues)
    }

    private fun processCustomStrategy(
        transactions: List<TransactionModel>,
        uiState: AnalyticUiState,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        val start = uiState.startDate
        val end = uiState.endDate
        val days = start.daysUntil(end)
        return when (days) {
            in 0..15 -> processRecentStrategy(transactions, timeZone)
            in 16..31 -> processBiDailyStrategy(transactions, timeZone)
            in 32..365 -> processMonthlyStrategy(transactions, uiState, timeZone)
            else -> processYearlyStrategy(transactions, timeZone)
        }
    }

    private fun processBiDailyStrategy(
        transactions: List<TransactionModel>,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        val startDate = localDateNow().minus(DatePeriod(days = 30)) // 示例起始日期
        val endDate = localDateNow()
        val dateSequence = generateDateSequence(startDate, endDate, stepDay = 2)

        val incomeByDate = dateSequence.associateWith { 0.0 }.toMutableMap()
        val expenseByDate = dateSequence.associateWith { 0.0 }.toMutableMap()

        transactions.forEach { txn ->
            val date = txn.transactionInstant.toLocalDateTime(timeZone).date
            val mappedDate = LocalDate(
                date.year,
                date.monthNumber,
                if (date.dayOfMonth % 2 == 0) date.dayOfMonth - 1 else date.dayOfMonth
            )
            if (mappedDate in incomeByDate) {
                if (txn.transactionType == TransactionType.INCOME) {
                    incomeByDate[mappedDate] =
                        incomeByDate[mappedDate]!! + txn.amount.toPlainString().toDouble()
                } else if (txn.transactionType == TransactionType.EXPENSE) {
                    expenseByDate[mappedDate] =
                        expenseByDate[mappedDate]!! + txn.amount.toPlainString().toDouble()
                }
            }
        }

        val xAxisLabels = dateSequence.map { "${it.monthNumber}-${it.dayOfMonth}" }
        val incomeValues = dateSequence.map { incomeByDate[it] ?: 0.0 }
        val expenseValues = dateSequence.map { expenseByDate[it] ?: 0.0 }

        return Triple(xAxisLabels, incomeValues, expenseValues)
    }

    fun processCategoryPieChartData(
        transactions: List<TransactionModel>
    ): Pair<List<PieChartData>, List<PieChartData>> {
        val incomeTransactions =
            transactions.filter { it.transactionType == TransactionType.INCOME }
        val expenseTransactions =
            transactions.filter { it.transactionType == TransactionType.EXPENSE }

        val incomePieData = incomeTransactions.groupBy { it.category.parentCategory!! }
            .map { (category, txns) ->
                PieChartData(
                    data = txns.sumOf { it.amount.toPlainString().toDouble() },
                    partName = category.categoryName.toString(),
                    color = generateRandomColor()
                )
            }

        val expensePieData = expenseTransactions.groupBy { it.category.parentCategory!! }
            .map { (category, txns) ->
                PieChartData(
                    data = txns.sumOf { it.amount.toPlainString().toDouble() },
                    partName = category.name,
                    color = generateRandomColor()
                )
            }

        return Pair(incomePieData, expensePieData)
    }

    fun processAssetTableData(
        transactions: List<TransactionModel>
    ): List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>> {
        val timeZone = TimeZone.currentSystemDefault()
        return transactions.groupBy { it.transactionInstant.toLocalDateTime(timeZone).date }
            .map { (date, txns) ->
                val income = txns.filter { it.transactionType == TransactionType.INCOME }
                    .fold(BigDecimal.ZERO) { acc, txn -> acc + txn.amount }
                val expense = txns.filter { it.transactionType == TransactionType.EXPENSE }
                    .fold(BigDecimal.ZERO) { acc, txn -> acc + txn.amount }
                val balance = income - expense
                date to Triple(income, expense, balance)
            }
    }

    fun processAssetTrendLineChartData(
        assetTableData: List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>>
    ): Pair<List<String>, List<Double>> {
        val xAxisLabels = assetTableData.map { it.first.toString() }
        val assetValues = assetTableData.map { it.second.third.toPlainString().toDouble() }
        return Pair(xAxisLabels, assetValues)
    }

    private fun generateRandomColor(): Color {
        return Color(
            red = Random.nextInt(0, 255),
            green = Random.nextInt(0, 255),
            blue = Random.nextInt(0, 255)
        )
    }
}

enum class ProcessStrategy {
    RECENT, MONTHLY, YEARLY, CUSTOM
}


