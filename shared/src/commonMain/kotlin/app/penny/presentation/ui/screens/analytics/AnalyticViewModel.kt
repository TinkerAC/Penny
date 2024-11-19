package app.penny.presentation.ui.screens.analytics

import androidx.compose.ui.graphics.Color
import app.penny.data.repository.UserDataRepository
import app.penny.domain.enum.TransactionType
import app.penny.domain.model.LedgerModel
import app.penny.domain.model.TransactionModel
import app.penny.domain.usecase.GetAllLedgerUseCase
import app.penny.domain.usecase.SearchTransactionsUseCase
import app.penny.utils.generateDateSequence
import app.penny.utils.generateMonthSequence
import app.penny.utils.getDaysInMonth
import app.penny.utils.localDateNow
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


class AnalyticViewModel(
    private val userDataRepository: app.penny.data.repository.UserDataRepository,
    private val getAllLedgerUseCase: GetAllLedgerUseCase,
    private val searchTransactionsUseCase: SearchTransactionsUseCase
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
                selectLedger(recentLedger!!) //
                prepareDataForCharts()

            } else {
                // 如果没有最近使用的账本，显示账本选择对话框
                showLedgerSelectionDialog()
            }
        }
        selectTab(AnalyticTab.Recent)
    }

    fun handleIntent(intent: AnalyticIntent) {
        when (intent) {
            is AnalyticIntent.OnTabSelected -> {
                selectTab(intent.tab)

            }

            is AnalyticIntent.OnYearSelected -> {
                selectYear(intent.year)
            }

            is AnalyticIntent.OnYearMonthSelected -> {
                selectYearMonth(intent.yearMonth)
            }

            is AnalyticIntent.OnStartDateSelected -> {
                _uiState.value = _uiState.value.copy(startDate = intent.date)

            }

            is AnalyticIntent.OnEndDateSelected -> {
                _uiState.value = _uiState.value.copy(endDate = intent.date)

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

    private fun selectYear(year: Int) {
        Logger.d("selected Year${year}")
        _uiState.value = _uiState.value.copy(selectedYear = year)

        if (_uiState.value.selectedYear != year) {
            _uiState.value = _uiState.value.copy(selectedYear = year)
        }

        //设置时间区间
        _uiState.value = _uiState.value.copy(
            startDate = LocalDate(year, 1, 1),
            endDate = LocalDate(year, 12, 31)
        )


        performFilter()
    }

    private fun selectYearMonth(yearMonth: YearMonth) {
        Logger.d("selected YearMonth${yearMonth}")
        if (yearMonth !== _uiState.value.selectedYearMonth) {
            //update the selectedYearMonth
            _uiState.value = _uiState.value.copy(
                selectedYearMonth = yearMonth,

                )
        }
        //set the startDate and endDate
        _uiState.value = _uiState.value.copy(
            startDate = LocalDate(yearMonth.year, yearMonth.month, 1),
            endDate = LocalDate(
                yearMonth.year,
                yearMonth.month,
                getDaysInMonth(yearMonth.year, yearMonth.month)
            )
        )
        performFilter()
    }


    private fun selectTab(tab: AnalyticTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        // 设定新的StartDate 和 EndDate

        //设置时间区间
        when (tab) {
            //最近7天,直接设置_uiState的startDate和endDate
            AnalyticTab.Recent -> {
                _uiState.value = _uiState.value.copy(
                    startDate = localDateNow().minus(DatePeriod(days = 7)), endDate = localDateNow()
                )
            }
            //按月统计,通过selectYearMonth设置startDate和endDate
            AnalyticTab.Monthly -> {
                //从_uiState 中获取上次选择的年月,并跟新时间区间
                selectYearMonth(_uiState.value.selectedYearMonth)
            }

            //按年统计,通过selectYear设置startDate和endDate
            AnalyticTab.Yearly -> {
                selectYear(_uiState.value.selectedYear)
            }
            //自定义时间,通过界面上的TimePicker设置startDate和endDate
            AnalyticTab.Custom -> {

            }
        }

        performFilter()


    }

    private fun performFilter() {
        //从数据库获取对应的ledgerId和StartDate和EndDate之间的交易记录，赋值给_uiState的filteredTransactions
        if (_uiState.value.selectedLedger != null) {
            //从数据库获取对应的ledgerId和StartDate和EndDate之间的交易记录
            screenModelScope.launch {

                val transactions = searchTransactionsUseCase(
                    ledgerId = _uiState.value.selectedLedger!!.id,
                    startDate = _uiState.value.startDate,
                    endDate = _uiState.value.endDate
                )
                _uiState.value = _uiState.value.copy(filteredTransactions = transactions)
            }
        } else {
            //如果没有选择ledger,设置filteredTransactions为空
            _uiState.value = _uiState.value.copy(filteredTransactions = emptyList())
        }

        prepareDataForCharts()
    }


    private fun showLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = true)
    }

    private fun dismissLedgerSelectionDialog() {
        _uiState.value = _uiState.value.copy(ledgerSelectionDialogVisible = false)
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
        }
        //redo selectTab
        selectTab(_uiState.value.selectedTab)
    }


    private fun prepareDataForCharts() {
        processIncomeExpenseTrendLineChartData(
            transactions = _uiState.value.filteredTransactions,
            strategy = when (_uiState.value.selectedTab) {
                AnalyticTab.Recent -> ProcessStrategy.RECENT
                AnalyticTab.Monthly -> ProcessStrategy.MONTHLY
                AnalyticTab.Yearly -> ProcessStrategy.YEARLY
                AnalyticTab.Custom -> ProcessStrategy.CUSTOM
            }
        )
        val (incomePieChartData, expensePieChartData) = processCategoryPieChartData(
            transactions = _uiState.value.filteredTransactions
        )


        val assetDailyChangeTableData = processAssetTableData(
            transactions = _uiState.value.filteredTransactions
        )


        val assetTrendLineChartData = processAssetTrendLineChartData(assetDailyChangeTableData)

        _uiState.value = _uiState.value.copy(
            incomePieChartData = incomePieChartData,
            expensePieChartData = expensePieChartData,
            assetChangeTableData = assetDailyChangeTableData,
            assetTrendLineChartData = assetTrendLineChartData
        )
    }


    //
    private fun processIncomeExpenseTrendLineChartData(
        transactions: List<TransactionModel>, strategy: ProcessStrategy
    ): Unit {
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


                for ((date, transactionsInGroup) in groupedTransactions) {
                    for (transaction in transactionsInGroup) {
                        val amount = transaction.amount.toPlainString().toDouble()
                        when (transaction.transactionType) {
                            TransactionType.INCOME -> {

                                incomeByDate[date] = (incomeByDate[date] ?: 0.0) + amount

                            }

                            TransactionType.EXPENSE -> {
                                expenseByDate[date] = (expenseByDate[date] ?: 0.0) + amount
                            }

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
                val startDate = _uiState.value.startDate
                val endDate = _uiState.value.endDate
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
                //根据时间跨度决定分组方式
                val start = _uiState.value.startDate
                val end = _uiState.value.endDate
                val days = start.daysUntil(end)
                when (days) {
                    in 0..15 -> {
                        //按天分组
                        val dateSequence = generateDateSequence(start, end)
                        val incomeByDate = mutableMapOf<LocalDate, Double>()
                        val expenseByDate = mutableMapOf<LocalDate, Double>()

                        dateSequence.forEach { date ->
                            incomeByDate[date] = 0.0
                            expenseByDate[date] = 0.0
                        }
                        val groupedTransactions = transactions.groupBy {
                            it.transactionDate.toLocalDateTime(timeZone).date
                        }

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
                                }
                            }
                        }



                        xAxisLabels = dateSequence.map { localTimeMappingFunction(it) }
                        incomeValues = dateSequence.map { incomeByDate[it] ?: 0.0 }
                        expenseValues = dateSequence.map { expenseByDate[it] ?: 0.0 }
                    }

                    in 16..31 -> {
                        //每2天一组

                        val dateSequence = generateDateSequence(start, end, stepDay = 2)
                        val incomeByDate = mutableMapOf<LocalDate, Double>()
                        val expenseByDate = mutableMapOf<LocalDate, Double>()

                        dateSequence.forEach { date ->
                            incomeByDate[date] = 0.0
                            expenseByDate[date] = 0.0
                        }

                        val groupedTransactions = transactions.groupBy {
                            mappingDayOfMonth(
                                mappingDayOfMonth(
                                    it.transactionDate.toLocalDateTime(
                                        timeZone
                                    ).date
                                )
                            )
                        }

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
                                }
                            }
                        }



                        xAxisLabels = dateSequence.map { localTimeMappingFunction(it) }
                        incomeValues = dateSequence.map { incomeByDate[it] ?: 0.0 }
                        expenseValues = dateSequence.map { expenseByDate[it] ?: 0.0 }


                    }

                    //按月分组
                    in 32..365 -> {
                        val startMonth = start.monthNumber
                        val endMonth = end.monthNumber


                        val yearMonthSequence = generateMonthSequence(start, end)

                        val incomeByMonth = mutableMapOf<YearMonth, Double>()
                        val expenseByMonth = mutableMapOf<YearMonth, Double>()

                        yearMonthSequence.forEach { yearMonth ->
                            incomeByMonth[yearMonth] = 0.0
                            expenseByMonth[yearMonth] = 0.0
                        }


                        val groupedTransactions = transactions.groupBy {
                            YearMonth(
                                it.transactionDate.toLocalDateTime(timeZone).date.year,
                                it.transactionDate.toLocalDateTime(timeZone).date.monthNumber
                            )
                        }

                        groupedTransactions.forEach { (yearMonth, transactionsInGroup) ->
                            transactionsInGroup.forEach { transaction ->
                                val amount = transaction.amount.toPlainString().toDouble()
                                when (transaction.transactionType) {
                                    TransactionType.INCOME -> {
                                        incomeByMonth[yearMonth] =
                                            (incomeByMonth[yearMonth] ?: 0.0) + amount
                                    }

                                    TransactionType.EXPENSE -> {
                                        expenseByMonth[yearMonth] =
                                            (expenseByMonth[yearMonth] ?: 0.0) + amount
                                    }
                                }
                            }
                        }


                        xAxisLabels = yearMonthSequence.map { "${it.year}-${it.month}" }
                        incomeValues = yearMonthSequence.map { incomeByMonth[it] ?: 0.0 }
                        expenseValues = yearMonthSequence.map { expenseByMonth[it] ?: 0.0 }


                    }


                    in 366..Int.MAX_VALUE -> {
                        //按年分组
                        val startYear = start.year
                        val endYear = end.year

                        val years = (startYear..endYear).toList()

                        val incomeByYear = mutableMapOf<Int, Double>()
                        val expenseByYear = mutableMapOf<Int, Double>()

                        years.forEach { year ->
                            incomeByYear[year] = 0.0
                            expenseByYear[year] = 0.0
                        }

                        val groupedTransactions = transactions.groupBy {
                            it.transactionDate.toLocalDateTime(timeZone).date.year
                        }

                        groupedTransactions.forEach { (year, transactionsInGroup) ->
                            transactionsInGroup.forEach { transaction ->
                                val amount = transaction.amount.toPlainString().toDouble()
                                when (transaction.transactionType) {
                                    TransactionType.INCOME -> {
                                        incomeByYear[year] = (incomeByYear[year] ?: 0.0) + amount
                                    }

                                    TransactionType.EXPENSE -> {
                                        expenseByYear[year] = (expenseByYear[year] ?: 0.0) + amount
                                    }
                                }
                            }
                        }

                        xAxisLabels = years.map { it.toString() }
                        incomeValues = years.map { incomeByYear[it] ?: 0.0 }
                        expenseValues = years.map { expenseByYear[it] ?: 0.0 }
                    }
                }


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


    private fun processCategoryPieChartData(
        transactions: List<TransactionModel>
    ): Pair<List<PieChartData>, List<PieChartData>> {

        val incomeTransactions =
            transactions.filter { it.transactionType == TransactionType.INCOME }
        val expenseTransactions =
            transactions.filter { it.transactionType == TransactionType.EXPENSE }


        val incomeLevel1CategoryPieChartData = incomeTransactions.groupBy {
            it.category.parentCategory!!
        }.map {
            PieChartData(
                data = it.value.sumOf { transaction ->
                    transaction.amount.toPlainString().toDouble()
                },
                partName = it.key.name,
                //random color
                color = Color(
                    Random.nextInt(0, 255),
                    Random.nextInt(0, 255),
                    Random.nextInt(0, 255)
                )
            )
        }


        val expenseLevel1CategoryPieChartData = expenseTransactions.groupBy {
            it.category.parentCategory!!
        }.map {
            PieChartData(
                data = it.value.sumOf { transaction ->
                    transaction.amount.toPlainString().toDouble()
                },
                partName = it.key.name,
                //random color
                color = Color(
                    Random.nextInt(0, 255),
                    Random.nextInt(0, 255),
                    Random.nextInt(0, 255)
                )
            )
        }
        return Pair(incomeLevel1CategoryPieChartData, expenseLevel1CategoryPieChartData)
    }


    private fun processAssetTrendLineChartData(
        assetChangeTableData: List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>>
    ): Pair<List<String>, List<Double>> {


        val xAxisLabels = assetChangeTableData.map { it.first.toString() }
        val assetValues = assetChangeTableData.map { it.second.third }

        return xAxisLabels to assetValues.map { it.toPlainString().toDouble() }


    }


    /**
     * 处理资产表数据
     * @return Pair<LocalDate, Triple<Double, Double, Double>>
     *     第一个元素是日期, 第二个元素是日收入、日支出、日结余
     */
    private fun processAssetTableData(
        transactions: List<TransactionModel>
    ): List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>> {

        val groupedTransactions = transactions.groupBy {
            it.transactionDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

        val assetTableData =
            mutableListOf<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>>()
        for ((date, transactionsInGroup) in groupedTransactions) {
            val income = transactionsInGroup.filter { it.transactionType == TransactionType.INCOME }
                .fold(
                    BigDecimal.ZERO
                ) { acc, transactionModel ->
                    acc + transactionModel.amount
                }
            val expense =
                transactionsInGroup.filter { it.transactionType == TransactionType.EXPENSE }
                    .fold(
                        BigDecimal.ZERO
                    ) { acc, transactionModel ->
                        acc + transactionModel.amount
                    }
            val balance = income - expense
            assetTableData.add(date to Triple(income, expense, balance))
        }

        return assetTableData


    }


}

enum class ProcessStrategy {
    RECENT, MONTHLY, YEARLY, CUSTOM
}