// file: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionViewModel.kt
package app.penny.feature.transactions

import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.valueObject.YearMonth
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi

/**
 * TransactionViewModel负责从仓库中获取数据、更新UI状态，并响应用户意图
 */
class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()



    fun refreshData() {
        screenModelScope.launch {
            try {
                // 获取当前用户
                val user = userRepository.findCurrentUser()
                _uiState.update { it.copy(user = user) }
                // 获取交易数据
                fetchTransactions()
                // 默认选择分组选项，例如按天分组
                handleIntent(TransactionIntent.SelectGroupByOption(GroupByType.items[0].options[0]))
            } catch (e: Exception) {
                Logger.e("Initialization error: ${e.message}")
                _uiState.update { it.copy(errorMessage = e.message ?: "未知错误") }
            }
        }
    }




    init {
        screenModelScope.launch {
            try {
                // 获取当前用户
                val user = userRepository.findCurrentUser()
                _uiState.update { it.copy(user = user) }

                // 获取交易数据
                fetchTransactions()

                // 默认选择分组选项，例如按天分组
                handleIntent(TransactionIntent.SelectGroupByOption(GroupByType.items[0].options[0]))
            } catch (e: Exception) {
                Logger.e("Initialization error: ${e.message}")
                _uiState.update { it.copy(errorMessage = e.message ?: "未知错误") }
            }
        }
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
        Logger.d("Toggle view to ${if (_uiState.value.isCalendarView) "Calendar" else "List"}")
    }

    /**
     * 选择日期
     */
    private fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        Logger.d("Selected date: $date")
    }


    /**
     * 选择分组选项
     */
    private fun selectGroupByOption(groupByOption: GroupByType.GroupOption) {
        _uiState.update {
            it.copy(
                selectedGroupByOption = groupByOption,
                selectedGroupByType = when (groupByOption) {
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
                val userUuid = _uiState.value.user?.uuid
                if (userUuid != null) {
                    val transactions = transactionRepository.findByUserUuid(userUuid)
                    _uiState.update { it.copy(transactions = transactions, isLoading = false) }
                    Logger.d("Fetched ${transactions.size} transactions")

                    // 计算总收入、支出和结余
                    calculateTotals(transactions)

                    // 如果已选择分组选项，则重新分组
                    _uiState.value.selectedGroupByOption?.let { doGroupBy(it) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "用户未登录") }
                    Logger.e("User UUID is null")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "数据获取失败"
                    )
                }
                Logger.e("Error fetching transactions: ${e.message}")
            }
        }
    }

    /**
     * 获取指定月份的交易数据
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun fetchTransactionsForMonth(yearMonth: YearMonth) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            try {
                val userUuid = _uiState.value.user?.uuid
                if (userUuid != null) {
                    val transactions =
                        transactionRepository.findByUserAndYearMonth(userUuid, yearMonth)
                    _uiState.update { it.copy(transactions = transactions, isLoading = false) }
                    Logger.d("Fetched ${transactions.size} transactions for $yearMonth")

                    // 计算总收入、支出和结余
                    calculateTotals(transactions)

                    // 重新分组
                    _uiState.value.selectedGroupByOption?.let { doGroupBy(it) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "用户未登录") }
                    Logger.e("User UUID is null")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "数据获取失败"
                    )
                }
                Logger.e("Error fetching transactions for month: ${e.message}")
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
     * 按时间分组
     */
    private fun groupByTime(groupBy: GroupByType.Time.GroupOption): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions
        val groupedMap = transactions.groupBy { transaction ->
            val zonedDateTime =
                transaction.transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            when (groupBy) {
                GroupByType.Time.GroupOption.Day ->
                    "${zonedDateTime.year}-${
                        zonedDateTime.monthNumber.toString().padStart(2, '0')
                    }-${zonedDateTime.dayOfMonth.toString().padStart(2, '0')}"

                GroupByType.Time.GroupOption.Week -> {
                    val weekOfYear = ((zonedDateTime.dayOfYear - 1) / 7) + 1
                    "${zonedDateTime.year}-W${weekOfYear.toString().padStart(2, '0')}"
                }

                GroupByType.Time.GroupOption.Month ->
                    "${zonedDateTime.year}-${
                        zonedDateTime.monthNumber.toString().padStart(2, '0')
                    }"

                GroupByType.Time.GroupOption.Season -> {
                    val season = when (zonedDateTime.monthNumber) {
                        in 1..3 -> "Q1"
                        in 4..6 -> "Q2"
                        in 7..9 -> "Q3"
                        else -> "Q4"
                    }
                    "${zonedDateTime.year}-$season"
                }

                GroupByType.Time.GroupOption.Year -> "${zonedDateTime.year}"
            }
        }

        val sortedGroups = groupedMap.entries.sortedByDescending { it.key }
        return sortedGroups.map { (key, transactions) ->
            GroupedTransaction(
                groupKey = key,
                transactions = transactions.sortedByDescending { it.transactionInstant },
                balance = calculateBalance(transactions),
                income = calculateIncome(transactions),
                expense = calculateExpense(transactions)
            )
        }
    }

    /**
     * 按类别分组
     */
    private fun groupByCategory(groupBy: GroupByType.Category.GroupOption): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions
        val groupedMap = transactions.groupBy { transaction ->
            when (groupBy) {
                GroupByType.Category.GroupOption.Primary
                    -> transaction.category.getLevel1Category().name

                GroupByType.Category.GroupOption.Secondary
                    -> transaction.category.name
            }
        }

        val sortedGroups = groupedMap.entries.sortedByDescending { it.key }
        return sortedGroups.map { (key, transactions) ->
            GroupedTransaction(
                groupKey = key,
                transactions = transactions.sortedByDescending { it.transactionInstant },
                balance = calculateBalance(transactions),
                income = calculateIncome(transactions),
                expense = calculateExpense(transactions)
            )
        }
    }

    /**
     * 按账本分组
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun groupByLedger(): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions
        val groupedMap = transactions.groupBy { it.ledgerUuid.toString() }
        val sortedGroups = groupedMap.entries.sortedByDescending { it.key }
        return sortedGroups.map { (key, transactions) ->
            GroupedTransaction(
                groupKey = key,
                transactions = transactions.sortedByDescending { it.transactionInstant },
                balance = calculateBalance(transactions),
                income = calculateIncome(transactions),
                expense = calculateExpense(transactions)
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

    /**
     * 计算整体总收入、总支出和结余
     */
    private fun calculateTotals(transactions: List<TransactionModel>) {
        val totalIncome = calculateIncome(transactions)
        val totalExpense = calculateExpense(transactions)
        val totalBalance = totalIncome - totalExpense

        _uiState.update {
            it.copy(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                totalBalance = totalBalance
            )
        }
        Logger.d("Calculated totals - Income: $totalIncome, Expense: $totalExpense, Balance: $totalBalance")
    }
}
