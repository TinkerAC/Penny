// file: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionViewModel.kt
package app.penny.feature.transactions

import app.penny.core.data.repository.StatisticRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.enumerate.TransactionType
import app.penny.core.domain.model.GroupIdentifier
import app.penny.core.domain.model.Summary
import app.penny.core.domain.model.TransactionModel
import app.penny.core.utils.localDateNow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi

/**
 * TransactionViewModel负责从仓库中获取数据、更新UI状态，并响应用户意图
 */
class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val statisticRepository: StatisticRepository,
    private val userDataRepository: UserDataRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()


    fun refreshData() {
        screenModelScope.launch {
            try {
                // 获取当前用户
                val user = userDataRepository.getUser()

                _uiState.update { it.copy(user = user) }
                // 获取交易数据
                fetchTransactions()
                // 默认选择分组选项，例如按天分组
                handleIntent(TransactionIntent.SelectGroupByOption(GroupByType.items[0].options[0]))

                _uiState.value = _uiState.value.copy(
                    totalSummary = statisticRepository.getUserTotalSummary(user),
                )

            } catch (e: Exception) {
                Logger.e("Initialization error: ${e.message}")
                _uiState.update { it.copy(errorMessage = e.message ?: "未知错误") }
            }
        }
    }


    init {
        refreshData()
    }

    /**
     * 处理用户意图
     */
    fun handleIntent(intent: TransactionIntent) {
        when (intent) {
            is TransactionIntent.SelectGroupByOption -> selectGroupByOption(intent.groupByOption)
            is TransactionIntent.ToggleView -> toggleView()
            is TransactionIntent.SelectDate -> selectDate(intent.date)
        }
    }

    /**
     * 切换视图（列表/日历）
     */
    private fun toggleView() {
        _uiState.update { currentState ->
            currentState.copy(
                isCalendarView = !currentState.isCalendarView,
                selectedDate = if (currentState.isCalendarView) null else currentState.selectedDate
            )
        }

        // click today after every time get into calendar view

        if (_uiState.value.isCalendarView) {
            selectDate(localDateNow())
        }


        Logger.d("Toggle view to ${if (_uiState.value.isCalendarView) "Calendar" else "List"}")
    }

    /**
     * 选择日期
     */
    private fun selectDate(date: LocalDate) {
        screenModelScope.launch { //fetch transactions for the selected date
            _uiState.value = _uiState.value.copy(
                selectedDate = date,
                calendarViewSummaryByDate = _uiState.value.transactions.groupBy {
                    it.transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }.mapValues { entry ->
                    val income = entry.value.filter { it.amount > BigDecimal.ZERO }
                        .fold(BigDecimal.ZERO) { acc, transaction -> acc + transaction.amount }
                    val expense = entry.value.filter { it.amount < BigDecimal.ZERO }
                        .fold(BigDecimal.ZERO) { acc, transaction -> acc + transaction.amount.abs() }

                    Summary(
                        income = income,
                        expense = expense,
                        balance = income - expense
                    )
                },


                calendarViewTransactionOfDate = transactionRepository.findByUser(
                    user = userDataRepository.getUser()
                ).filter {
                    it.transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                }
            )


        }
    }


    /**
     * 选择分组选项
     */
    private fun selectGroupByOption(groupByOption: GroupByType.GroupOption) {
        _uiState.update {
            it.copy(
                selectedGroupByOption = groupByOption, selectedGroupByType = when (groupByOption) {
                    is GroupByType.Time.GroupOption -> GroupByType.Time
                    is GroupByType.Category.GroupOption -> GroupByType.Category
                }

            )
        }
        doGroupBy(groupByOption)
        Logger.d("Selected group by option: $groupByOption")
    }

    /**
     * 隐藏分组下拉菜单
     */
    private fun dismissSharedDropdown() {
        _uiState.update { it.copy(sharedPopUpVisible = false) }
        Logger.d("Dismiss shared dropdown")
    }

    /**
     * 获取交易数据
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun fetchTransactions() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            try {
                val transactions = transactionRepository.findByUser(
                    user = userDataRepository.getUser()
                )
                _uiState.update {
                    it.copy(
                        transactions = transactions,
                        isLoading = false
                    )
                }
                Logger.d("Fetched ${transactions.size} transactions")
                _uiState.value.selectedGroupByOption?.let { doGroupBy(it) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = e.message ?: "数据获取失败"
                    )
                }
                Logger.e("Error fetching transactions: ${e.message}")
            }
        }
    }


    /**
     * 执行分组操作
     */
    private fun doGroupBy(groupByOption: GroupByType.GroupOption) {
        val grouped = when (groupByOption) {
            is GroupByType.Time.GroupOption -> groupByTime(groupByOption)
            is GroupByType.Category.GroupOption -> groupByCategory(groupByOption)

        }
        _uiState.update { it.copy(groupedTransactions = grouped) }
        Logger.d("Grouped transactions by $groupByOption")
    }

    /**
     * 按时间维度进行分组
     */
    private fun groupByTime(groupBy: GroupByType.Time.GroupOption): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions

        // 1) 根据交易的时间和分组方式 (Day, Week, Month, Season, Year)，
        //    调用工厂方法创建 "TimeGroupIdentifier" 来作为分组键
        val groupedMap = transactions.groupBy { transaction ->
            val zonedDateTime: LocalDateTime =
                transaction.transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            when (groupBy) {
                GroupByType.Time.GroupOption.Day -> {
                    GroupIdentifier.TimeGroupIdentifier(
                        groupOption = groupBy,
                        year = zonedDateTime.year,
                        month = zonedDateTime.monthNumber,
                        day = zonedDateTime.dayOfMonth
                    )
                }

                GroupByType.Time.GroupOption.Week -> {
                    GroupIdentifier.TimeGroupIdentifier(
                        groupOption = groupBy,
                        year = zonedDateTime.year,
                        weekOfYear = zonedDateTime.weekOfYear(),
                    )
                }

                GroupByType.Time.GroupOption.Month -> {
                    GroupIdentifier.TimeGroupIdentifier(
                        groupOption = groupBy,
                        year = zonedDateTime.year,
                        month = zonedDateTime.monthNumber
                    )
                }

                GroupByType.Time.GroupOption.Season -> {
                    GroupIdentifier.TimeGroupIdentifier(
                        groupOption = groupBy,
                        year = zonedDateTime.year,
                        quarter = (zonedDateTime.monthNumber - 1) / 3 + 1
                    )
                }

                GroupByType.Time.GroupOption.Year -> {
                    GroupIdentifier.TimeGroupIdentifier(
                        groupOption = groupBy, year = zonedDateTime.year
                    )
                }
            }
        }

        // 2) 排序。这里示例中只是简单地根据 "displayString" 做倒序；你也可以改成先 year 再 month 再 day 的多级排序
        val sortedGroups = groupedMap.entries.sortedByDescending { it.key.displayString }

        // 3) 构造成 GroupedTransaction
        return sortedGroups.map { (groupIdentifier, listOfTx) ->
            // groupIdentifier 就是 "TimeGroupIdentifier"
            // 如果你用工厂方法，类型是 GroupIdentifier，但其实际子类型是 TimeGroupIdentifier
            GroupedTransaction(
                groupIdentifier = groupIdentifier,
                transactions = listOfTx.sortedByDescending { it.transactionInstant },
                summary = Summary(
                    balance = calculateBalance(listOfTx),
                    income = calculateIncome(listOfTx),
                    expense = calculateExpense(listOfTx)
                )
            )
        }
    }


    /**
     * 按类别分组
     */
    private fun groupByCategory(
        groupBy: GroupByType.Category.GroupOption
    ): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions

        // 1) 分组依据：根据 groupBy 来确定“一级类别”还是“二级类别”
        val groupedMap = transactions.groupBy { transaction ->
            when (groupBy) {
                GroupByType.Category.GroupOption.Primary ->
                    // 比如 getLevel1Category() 返回的是一个 Category 对象
                    transaction.category.getLevel1Category()

                GroupByType.Category.GroupOption.Secondary ->
                    // 自身 category
                    transaction.category
            }
        }

        // 2) 排序：根据分组后的“Category 对象”进行排序，
        //    这里示例中使用 categoryName 做倒序排列
        val sortedGroups = groupedMap.entries.sortedByDescending { entry ->
            entry.key.name
        }

        // 3) 构建 GroupedTransaction：
        //    - 使用 CategoryGroupIdentifier 来存储分组信息
        return sortedGroups.map { (categoryObj, groupedTransactions) ->
            GroupedTransaction(
                groupIdentifier = GroupIdentifier.CategoryGroupIdentifier(
                    groupOption = groupBy, category = categoryObj
                ),
                transactions = groupedTransactions.sortedByDescending { it.transactionInstant },
                summary = Summary(
                    balance = calculateBalance(groupedTransactions),
                    income = calculateIncome(groupedTransactions),
                    expense = calculateExpense(groupedTransactions)
                )
            )
        }


    }

    /**
     * 计算总余额
     */
    private fun calculateBalance(transactions: List<TransactionModel>): BigDecimal {
        return transactions.fold(BigDecimal.ZERO) { acc, transaction ->
            acc + transaction.amount
        }
    }

    /**
     * 计算总收入
     */
    private fun calculateIncome(transactions: List<TransactionModel>): BigDecimal {
        return transactions.filter { it.transactionType == TransactionType.INCOME }
            .fold(BigDecimal.ZERO) { acc, transaction ->
                acc + transaction.amount
            }
    }

    /**
     * 计算总支出
     */
    private fun calculateExpense(transactions: List<TransactionModel>): BigDecimal {
        return transactions.filter { it.transactionType == TransactionType.EXPENSE }
            .fold(BigDecimal.ZERO) { acc, transaction ->
                acc + transaction.amount.abs()
            }
    }

}


/**
 * 扩展函数：获取 LocalDateTime 对应年份中的第几周
 */
fun LocalDateTime.weekOfYear(): Int {
    val firstDayOfYear = LocalDate(this.year, 1, 1) // 获取该年的第一天
    val firstDayOfWeek = firstDayOfYear.dayOfWeek // 获取该年的第一天是星期几

    // 计算该年的第一周的起始日期
    val firstWeekStart = if (firstDayOfWeek == DayOfWeek.MONDAY) {
        firstDayOfYear
    } else {
        // 找到第一个周一
        firstDayOfYear.plus(
            DatePeriod(
                days = 8 - firstDayOfWeek.ordinal
            )
        )
    }

    // 当前日期的 LocalDate
    val currentDate = this.date

    // 如果当前日期早于第一周起始日期，则属于上一年的最后一周
    if (currentDate < firstWeekStart) {
        return 0 // 可以视为第 0 周，或根据需求调整
    }

    // 计算当前日期和第一周起始日期之间的天数差
    val daysSinceFirstWeekStart = firstWeekStart.daysUntil(currentDate)

    // 天数差除以 7，并加 1，计算出第几周
    return (daysSinceFirstWeekStart / 7) + 1
}