package app.penny.feature.transactions

import app.penny.core.domain.usecase.GetAllTransactionsUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi

class TransactionViewModel(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        fetchTransactions()//TODO: replace with partial fetch
        //select the "Day" group by option by default
        handleIntent(TransactionIntent.SelectGroupByOption(GroupBy.Time.Day))

    }

    fun handleIntent(intent: TransactionIntent) {
        when (intent) {
            is TransactionIntent.ShowSharedDropdown -> showSharedDropdown(intent.groupByType)
            is TransactionIntent.SelectGroupByOption -> selectGroupByOption(intent.groupBy)
            is TransactionIntent.DismissSharedDropdown -> dismissSharedDropdown()
            is TransactionIntent.ToggleView -> toggleView()
            is TransactionIntent.SelectDate -> selectDate(intent.date)
        }
    }

    private fun toggleView() {
        _uiState.value = _uiState.value.copy(
            isCalendarView = !_uiState.value.isCalendarView,
            selectedDate = null // 切换视图时重置选中的日期
        )
        Logger.d("Toggle view to ${if (_uiState.value.isCalendarView) "Calendar" else "List"}")
    }

    private fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    private fun showSharedDropdown(
        groupByType: GroupByType,
        newSelectedGroupByOption: GroupBy? = null
    ) {
        val groupByOptions = GroupByType.getGroupByOptions(groupByType)

        _uiState.value = _uiState.value.copy(
            groupByOptions = groupByOptions,
            sharedPopUpVisible = groupByOptions.isNotEmpty()
        )
        newSelectedGroupByOption?.let { doGroupBy(it) }
    }

    private fun selectGroupByOption(groupBy: GroupBy) {
        _uiState.value = _uiState.value.copy(
            selectedGroupByOption = groupBy,
            selectedGroupByType = when (groupBy) {
                is GroupBy.Time -> GroupByType.Time
                is GroupBy.Category -> GroupByType.Category
                is GroupBy.Ledger -> GroupByType.Ledger
            }
        )
        dismissSharedDropdown()
        doGroupBy(groupBy)
    }

    private fun dismissSharedDropdown() {
        _uiState.value = _uiState.value.copy(sharedPopUpVisible = false)
    }

    /**
     * 根据选定的分组选项对交易进行分组
     */
    private fun doGroupBy(groupBy: GroupBy) {
        val groupedTransactions = when (groupBy) {
            is GroupBy.Time -> groupByTime(groupBy)
            is GroupBy.Category -> groupByCategory(groupBy)
            is GroupBy.Ledger -> groupByLedger()
        }
        Logger.d("Group by ${groupBy}, groupedTransactions: ${groupedTransactions.size}")
        _uiState.value = _uiState.value.copy(groupedTransactions = groupedTransactions)
    }

    private fun fetchTransactions() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val transactions = getAllTransactionsUseCase()
            _uiState.value = _uiState.value.copy(transactions = transactions, isLoading = false)
            // 初始分组
            _uiState.value.selectedGroupByOption?.let { doGroupBy(it) }
        }
    }

    /**
     * 按时间分组交易
     */
    private fun groupByTime(groupBy: GroupBy.Time): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions
        val groupedMap = transactions.groupBy { transaction ->
            val zonedDateTime =
                transaction.transactionDate.toLocalDateTime(TimeZone.currentSystemDefault())

            when (groupBy) {
                GroupBy.Time.Day -> {
                    "${zonedDateTime.year}-${
                        zonedDateTime.monthNumber.toString().padStart(2, '0')
                    }-${zonedDateTime.dayOfMonth.toString().padStart(2, '0')}"
                }

                GroupBy.Time.Week -> {
                    val weekOfYear = zonedDateTime.dayOfYear / 7 + 1
                    "${zonedDateTime.year}-W${weekOfYear.toString().padStart(2, '0')}"
                }

                GroupBy.Time.Month -> {
                    "${zonedDateTime.year}-${zonedDateTime.monthNumber.toString().padStart(2, '0')}"
                }

                GroupBy.Time.Season -> {
                    val season = when (zonedDateTime.monthNumber) {
                        in 1..3 -> "Q1"
                        in 4..6 -> "Q2"
                        in 7..9 -> "Q3"
                        else -> "Q4"
                    }
                    "${zonedDateTime.year}-$season"
                }

                GroupBy.Time.Year -> {
                    "${zonedDateTime.year}"
                }
            }
        }

        // 按时间降序排列分组
        val sortedGroups = groupedMap.entries.sortedByDescending { it.key }

        return sortedGroups.map { (key, value) ->
            GroupedTransaction(
                groupKey = key,
                transactions = value.sortedByDescending { it.transactionDate })
        }
    }

    /**
     * 按类别分组交易
     */
    private fun groupByCategory(groupBy: GroupBy.Category): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions
        val groupedMap = transactions.groupBy { transaction ->
            when (groupBy) {
                GroupBy.Category.Level1 -> {
                    transaction.category.getLevel1Category().categoryName
                }

                GroupBy.Category.Level2 -> {
                    transaction.category.categoryName
                }
            }
        }

        // 按字母顺序排列分组
        val sortedGroups = groupedMap.entries

        return sortedGroups.map { (key, value) ->
            GroupedTransaction(groupKey = key, transactions = value)
        }
    }

    /**
     * 按账本分组交易
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun groupByLedger(): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions
        val groupedMap = transactions.groupBy { transaction ->
            transaction.ledgerUuid.toString()
        }
        // 按字母顺序排列分组
        val sortedGroups = groupedMap.entries
        return sortedGroups.map { (key, value) ->
            GroupedTransaction(groupKey = key ?: "Unknown", transactions = value)
        }
    }
}