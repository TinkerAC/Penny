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
import app.penny.getRawStringResource
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import app.penny.presentation.ui.components.aayChart.donutChart.model.PieChartData
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

    /**
     * 初始化或手动刷新数据，比如在进入页面时调用
     */
    fun refreshData() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 加载用户的 ledger 列表
            loadUserLedgers()

            // 例如先自动选定一个默认的 ledger
            val defaultLedger = userDataRepository.getDefaultLedger()
            selectLedger(defaultLedger)

            // 默认进入 RecentTab
            selectTab(AnalyticTab.Recent)

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    /**
     * Intent 事件处理函数
     */
    fun handleIntent(intent: AnalyticIntent) {
        when (intent) {
            is AnalyticIntent.OnTabSelected -> selectTab(intent.tab)
            is AnalyticIntent.OnYearSelected -> {
                _uiState.value = _uiState.value.copy(selectedYear = intent.year)
                selectTab(AnalyticTab.Yearly)
            }

            is AnalyticIntent.OnYearMonthSelected -> {
                _uiState.value = _uiState.value.copy(selectedYearMonth = intent.yearMonth)
                selectTab(AnalyticTab.Monthly)
            }

            is AnalyticIntent.OnStartDateSelected -> updateDateRange(startDate = intent.date)
            is AnalyticIntent.OnEndDateSelected -> updateDateRange(endDate = intent.date)
            AnalyticIntent.ShowLedgerSelectionDialog -> showLedgerSelectionDialog()
            AnalyticIntent.DismissLedgerSelectionDialog -> dismissLedgerSelectionDialog()
            is AnalyticIntent.SelectLedger -> selectLedger(intent.ledger)
        }
    }

    /**
     * 从数据库载入用户的 Ledger 信息
     */
    private suspend fun loadUserLedgers() {
        val user = userDataRepository.getUser()
        val ledgers = ledgerRepository.findByUserUuid(user.uuid)
        _uiState.value = _uiState.value.copy(ledgers = ledgers)
    }

    /**
     * 选定指定 ledger
     */
    private fun selectLedger(ledger: LedgerModel) {
        screenModelScope.launch {
            Logger.d("Selected Ledger: ${ledger.uuid}")
            _uiState.value = _uiState.value.copy(selectedLedger = ledger)
            dismissLedgerSelectionDialog()

            // 当选择新的 Ledger 后，重新按当前 Tab 的逻辑刷新
            selectTab(_uiState.value.selectedTab)
        }
    }

    /**
     * 根据所选 Tab，统一更新时间范围，再做过滤
     */
    private fun selectTab(tab: AnalyticTab) {
        // 更新 UIState 中当前选中的 tab
        _uiState.value = _uiState.value.copy(selectedTab = tab)

        // 根据不同 Tab 的含义，计算对应的 startDate、endDate
        val (startDate, endDate) = determineDateRange(
            tab = tab,
            selectedYear = _uiState.value.selectedYear,
            selectedYearMonth = _uiState.value.selectedYearMonth,
            userStartDate = _uiState.value.startDate,
            userEndDate = _uiState.value.endDate
        )
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)

        // 真正开始过滤查询
        performFilter()
    }

    /**
     * 对外暴露的接口，用来更新自定义时间范围
     */
    private fun updateDateRange(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ) {
        val newStart = startDate ?: _uiState.value.startDate
        val newEnd = endDate ?: _uiState.value.endDate

        _uiState.value = _uiState.value.copy(startDate = newStart, endDate = newEnd)
        performFilter() // 根据新日期再次刷新数据
    }

    /**
     * 统一函数：根据当前 Tab（或 UIState 中的年份、月份等）来决定具体的时间区间
     */
    private fun determineDateRange(
        tab: AnalyticTab,
        selectedYear: Int,
        selectedYearMonth: YearMonth,
        userStartDate: LocalDate,
        userEndDate: LocalDate
    ): Pair<LocalDate, LocalDate> {

        return when (tab) {
            AnalyticTab.Recent -> {
                // 过去 7 天
                val end = localDateNow()
                val start = end.minus(DatePeriod(days = 7))
                start to end
            }

            AnalyticTab.Monthly -> {
                // 对应当前选中的月份
                val start = LocalDate(selectedYearMonth.year, selectedYearMonth.month, 1)
                val end = LocalDate(
                    selectedYearMonth.year,
                    selectedYearMonth.month,
                    getDaysInMonth(selectedYearMonth)
                )
                start to end
            }

            AnalyticTab.Yearly -> {
                // 对应当前选中的年
                val start = LocalDate(selectedYear, 1, 1)
                val end = LocalDate(selectedYear, 12, 31)
                start to end
            }

            AnalyticTab.Custom -> {
                // 用户自定义时间
                // 此处可以再校验一下若 start 大于 end 等异常情况
                userStartDate to userEndDate
            }
        }
    }

    /**
     * 根据当前 UIState 的时间范围与所选 Ledger 执行过滤查询
     */
    private fun performFilter() {
        val ledger = _uiState.value.selectedLedger ?: return
        val startDate = _uiState.value.startDate
        val endDate = _uiState.value.endDate

        screenModelScope.launch {
            val transactions = searchTransactionsUseCase(
                ledgerUuid = ledger.uuid,
                startDate = startDate,
                endDate = endDate
            )
            // 更新筛选结果到 uiState
            _uiState.value = _uiState.value.copy(filteredTransactions = transactions)

            // 计算图表所需数据
            prepareDataForCharts(transactions)
        }
    }

    /**
     * 根据当前的选中的 Tab（或时间跨度）来选择分组策略，然后生成所需图表数据
     */
    private fun prepareDataForCharts(transactions: List<TransactionModel>) {
        // 根据 UIState 和当前 Tab 来判定分组策略
        val strategy = determineGroupingStrategy(
            tab = _uiState.value.selectedTab,
            startDate = _uiState.value.startDate,
            endDate = _uiState.value.endDate
        )

        // 收/支趋势图
        val trendData = ChartDataProcessor.processIncomeExpenseTrendLineChartData(
            transactions = transactions,
            groupingStrategy = strategy,
            startDate = _uiState.value.startDate,
            endDate = _uiState.value.endDate
        )

        // 收/支类别饼图
        val (incomePieData, expensePieData) = ChartDataProcessor.processCategoryPieChartData(
            transactions = transactions,
        )

        // 资产变化表 & 资产趋势线
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

    /**
     * 根据 Tab（或自定义时间跨度）决定分组策略
     */
    private fun determineGroupingStrategy(
        tab: AnalyticTab,
        startDate: LocalDate,
        endDate: LocalDate
    ): GroupingStrategy {
        return when (tab) {
            AnalyticTab.Recent -> GroupingStrategy.DAILY  // 过去 7 天，按「日」分组
            AnalyticTab.Monthly -> GroupingStrategy.BI_DAILY  // 当前月，按「双日」分组
            AnalyticTab.Yearly -> GroupingStrategy.MONTHLY   // 当前年，按「月」分组
            AnalyticTab.Custom -> {
                // 自定义时间范围，根据跨度智能选择
                val days = startDate.daysUntil(endDate)
                when (days) {
                    in 0..15 -> GroupingStrategy.DAILY
                    in 16..31 -> GroupingStrategy.BI_DAILY
                    in 32..365 -> GroupingStrategy.MONTHLY
                    else -> GroupingStrategy.YEARLY
                }
            }
        }
    }

    /**
     * 显示 / 隐藏 Ledger 选择对话框
     */
    private fun showLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = true)
    }

    private fun dismissLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = false)
    }
}

/**
 * 独立的图表数据处理对象，不涉及 UIState，仅做数据加工
 */
object ChartDataProcessor {

    /**
     * 对外的主入口：根据策略(按日/双日/按月/按年等)来生成收入 / 支出趋势数据
     */
    fun processIncomeExpenseTrendLineChartData(
        transactions: List<TransactionModel>,
        groupingStrategy: GroupingStrategy,
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): IncomeExpenseTrendChartData {
        if (transactions.isEmpty()) {
            return IncomeExpenseTrendChartData.empty
        }

        val (xAxis, incomeValues, expenseValues) = when (groupingStrategy) {
            GroupingStrategy.DAILY -> {
                groupByDayInRange(transactions, startDate, endDate, timeZone)
            }

            GroupingStrategy.BI_DAILY -> {
                groupByBiDailyInRange(transactions, startDate, endDate, timeZone)
            }

            GroupingStrategy.MONTHLY -> {
                groupByMonthInRange(transactions, startDate, endDate, timeZone)
            }

            GroupingStrategy.YEARLY -> {
                groupByYearInRange(transactions, startDate, endDate, timeZone)
            }
        }

        return IncomeExpenseTrendChartData(
            xAxisData = xAxis,
            incomeValues = incomeValues,
            expenseValues = expenseValues
        )
    }

    /**
     * 在指定 [startDate, endDate] 区间内，生成按日分组的 X 轴 label，并计算收入 / 支出。
     */
    private fun groupByDayInRange(
        transactions: List<TransactionModel>,
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        // 生成每天的序列
        val dateSequence = generateDateSequence(startDate, endDate)

        // 初始化映射表
        val incomeByDay = dateSequence.associateWith { 0.0 }.toMutableMap()
        val expenseByDay = dateSequence.associateWith { 0.0 }.toMutableMap()

        // 累加数据
        transactions.forEach { txn ->
            val date = txn.transactionInstant.toLocalDateTime(timeZone).date
            if (date in incomeByDay) {
                val amount = txn.amount.toPlainString().toDouble()
                if (txn.transactionType == TransactionType.INCOME) {
                    incomeByDay[date] = incomeByDay[date]!! + amount
                } else {
                    expenseByDay[date] = expenseByDay[date]!! + amount
                }
            }
        }

        // 构造输出
        val xAxisLabels = dateSequence.map { "${it.monthNumber}-${it.dayOfMonth}" }
        val incomeValues = dateSequence.map { incomeByDay[it] ?: 0.0 }
        val expenseValues = dateSequence.map { expenseByDay[it] ?: 0.0 }

        return Triple(xAxisLabels, incomeValues, expenseValues)
    }

    /**
     * 在指定 [startDate, endDate] 区间内，生成按 "双日" 步长的 X 轴 label，并计算收入 / 支出。
     */
    private fun groupByBiDailyInRange(
        transactions: List<TransactionModel>,
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        // stepDay = 2, 可以支持跨月跨年
        val dateSequence = generateDateSequence(startDate, endDate, stepDay = 2)

        // 初始化映射表
        val incomeMap = dateSequence.associateWith { 0.0 }.toMutableMap()
        val expenseMap = dateSequence.associateWith { 0.0 }.toMutableMap()

        // 遍历交易记录并映射到对应的“双日”Slot
        transactions.forEach { txn ->
            val txnDate = txn.transactionInstant.toLocalDateTime(timeZone).date
            // 先把该日期 “对齐” 到相应的双日区间起点
            // 假设规则: 如果是偶数日 => dayOfMonth - 1, 否则维持不变（可根据你自己需求调整）
            val mappedDate = if ((txnDate.dayOfMonth % 2) == 0) {
                txnDate.minus(DatePeriod(days = 1))
            } else {
                txnDate
            }
            if (mappedDate in incomeMap) {
                val amount = txn.amount.toPlainString().toDouble()
                if (txn.transactionType == TransactionType.INCOME) {
                    incomeMap[mappedDate] = incomeMap[mappedDate]!! + amount
                } else {
                    expenseMap[mappedDate] = expenseMap[mappedDate]!! + amount
                }
            }
        }

        // 构造输出
        val xAxisLabels = dateSequence.map { "${it.monthNumber}-${it.dayOfMonth}" }
        val incomeValues = dateSequence.map { incomeMap[it] ?: 0.0 }
        val expenseValues = dateSequence.map { expenseMap[it] ?: 0.0 }
        return Triple(xAxisLabels, incomeValues, expenseValues)
    }


    /**
     * 在 [startDate, endDate] 区间内，按 (Year, Month) 分组。
     * 即每个 (year, month) 作为一个 X 轴节点。
     */
    private fun groupByMonthInRange(
        transactions: List<TransactionModel>,
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        // 1) 先生成 (Year, Month) 的序列
        val yearMonths = generateYearMonthSequence(startDate, endDate)

        // 2) 初始化: Map<(Int, Int), 0.0>，Key = Pair(year, month)
        val incomeByMonth = yearMonths.associateWith { 0.0 }.toMutableMap()
        val expenseByMonth = yearMonths.associateWith { 0.0 }.toMutableMap()

        // 3) 遍历交易，根据其日期所在的 year, month 进行聚合
        transactions.forEach { txn ->
            val localDate = txn.transactionInstant.toLocalDateTime(timeZone).date
            val yearMonthKey = (localDate.year to localDate.monthNumber)
            // 如果交易日期落在区间内，就累加
            if (yearMonthKey in incomeByMonth) {
                val amount = txn.amount.toPlainString().toDouble()
                if (txn.transactionType == TransactionType.INCOME) {
                    incomeByMonth[yearMonthKey] = incomeByMonth[yearMonthKey]!! + amount
                } else {
                    expenseByMonth[yearMonthKey] = expenseByMonth[yearMonthKey]!! + amount
                }
            }
        }

        // 4) 构造 xAxis 标签
        //    比如 "2023-01", "2023-02" ... "2024-05" 这样
        val xAxisLabels = yearMonths.map { (y, m) -> "$y-${m.toString().padStart(2, '0')}" }
        val incomeValues = yearMonths.map { incomeByMonth[it] ?: 0.0 }
        val expenseValues = yearMonths.map { expenseByMonth[it] ?: 0.0 }

        return Triple(xAxisLabels, incomeValues, expenseValues)
    }

    /**
     * 在 [startDate, endDate] 区间内，按 year 分组。
     */
    private fun groupByYearInRange(
        transactions: List<TransactionModel>,
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: TimeZone
    ): Triple<List<String>, List<Double>, List<Double>> {
        val years = (startDate.year..endDate.year).toList()
        val incomeByYear = years.associateWith { 0.0 }.toMutableMap()
        val expenseByYear = years.associateWith { 0.0 }.toMutableMap()

        transactions.forEach { txn ->
            val localDate = txn.transactionInstant.toLocalDateTime(timeZone).date
            val yearKey = localDate.year
            if (yearKey in incomeByYear && (localDate in startDate..endDate)) {
                val amount = txn.amount.toPlainString().toDouble()
                if (txn.transactionType == TransactionType.INCOME) {
                    incomeByYear[yearKey] = incomeByYear[yearKey]!! + amount
                } else {
                    expenseByYear[yearKey] = expenseByYear[yearKey]!! + amount
                }
            }
        }

        val xAxisLabels = years.map { it.toString() }
        val incomeValues = years.map { incomeByYear[it] ?: 0.0 }
        val expenseValues = years.map { expenseByYear[it] ?: 0.0 }
        return Triple(xAxisLabels, incomeValues, expenseValues)
    }

    /**
     * 饼图数据
     */
    fun processCategoryPieChartData(
        transactions: List<TransactionModel>
    ): Pair<List<PieChartData>, List<PieChartData>> {
        val incomeTxs = transactions.filter { it.transactionType == TransactionType.INCOME }
        val expenseTxs = transactions.filter { it.transactionType == TransactionType.EXPENSE }

        // 分组示例：按父类别聚合
        val incomePieData = incomeTxs
            .groupBy { it.category.parentCategory } // parentCategory 可能为 null，生产环境请注意判空
            .map { (category, txs) ->
                PieChartData(
                    data = txs.sumOf { it.amount.toPlainString().toDouble() },
                    partName = getRawStringResource(category!!.categoryName),
                    color = generateRandomColor()
                )
            }

        val expensePieData = expenseTxs
            .groupBy { it.category.parentCategory }
            .map { (category, txs) ->
                PieChartData(
                    data = txs.sumOf { it.amount.toPlainString().toDouble() },
                    partName = getRawStringResource(category!!.categoryName),
                    color = generateRandomColor()
                )
            }

        return incomePieData to expensePieData
    }

    /**
     * 资产表格数据
     */
    fun processAssetTableData(
        transactions: List<TransactionModel>
    ): List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>> {
        val timeZone = TimeZone.currentSystemDefault()
        return transactions
            .groupBy { it.transactionInstant.toLocalDateTime(timeZone).date }
            .map { (date, txns) ->
                val income = txns
                    .filter { it.transactionType == TransactionType.INCOME }
                    .fold(BigDecimal.ZERO) { acc, txn -> acc + txn.amount }
                val expense = txns
                    .filter { it.transactionType == TransactionType.EXPENSE }
                    .fold(BigDecimal.ZERO) { acc, txn -> acc + txn.amount }
                val balance = income - expense
                date to Triple(income, expense, balance)
            }
            .sortedBy { it.first } // 按日期升序
    }

    /**
     * 资产趋势图数据
     */
    fun processAssetTrendLineChartData(
        assetTableData: List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>>
    ): Pair<List<String>, List<Double>> {
        val xLabels = assetTableData.map { it.first.toString() }
        val yValues = assetTableData.map { it.second.third.toPlainString().toDouble() }
        return xLabels to yValues
    }

    private fun generateRandomColor(): Color {
        return Color(
            red = Random.nextInt(0, 256),
            green = Random.nextInt(0, 256),
            blue = Random.nextInt(0, 256)
        )
    }
}

/**
 * 根据业务含义定义的分组策略
 */
enum class GroupingStrategy {
    DAILY,
    BI_DAILY,   // 按双日
    MONTHLY,   // 按月
    YEARLY,    // 按年
}

/**
 * 按照年月递增，遍历从 startDate 到 endDate 之间的所有 (year, month)。
 */
fun generateYearMonthSequence(startDate: LocalDate, endDate: LocalDate): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    var currentYear = startDate.year
    var currentMonth = startDate.monthNumber

    while ((currentYear < endDate.year) ||
        (currentYear == endDate.year && currentMonth <= endDate.monthNumber)
    ) {
        result.add(currentYear to currentMonth)

        // month + 1
        currentMonth += 1
        if (currentMonth > 12) {
            currentMonth = 1
            currentYear += 1
        }
    }
    return result
}