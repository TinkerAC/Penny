// File: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/transactions/TransactionViewModel.kt
package app.penny.presentation.ui.screens.transactions

import app.penny.domain.usecase.GetAllTransactionsUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TransactionViewModel(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        fetchTransactions()
    }

    fun handleIntent(intent: TransactionIntent) {
        when (intent) {
            is TransactionIntent.SelectGroupByType -> selectGroupByType(intent.groupByType)
            is TransactionIntent.SelectGroupByOption -> selectGroupByOption(intent.groupBy)
            is TransactionIntent.DismissSharedDropdown -> dismissSharedDropdown()
        }
    }

    private fun selectGroupByType(groupByType: GroupByType) {
        val groupByOptions = GroupByType.getGroupByOptions(groupByType)
        val newSelectedGroupByOption = when {
            groupByOptions.isNotEmpty() -> groupByOptions.first()
            groupByType == GroupByType.Ledger -> GroupBy.Ledger
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            selectedGroupByType = groupByType,
            selectedGroupByOption = newSelectedGroupByOption,
            groupByOptions = groupByOptions,
            isSharedPopupVisible = groupByOptions.isNotEmpty()
        )

        newSelectedGroupByOption?.let { doGroupBy(it) }
    }

    private fun selectGroupByOption(groupBy: GroupBy) {
        _uiState.value = _uiState.value.copy(selectedGroupByOption = groupBy)
        dismissSharedDropdown()
        doGroupBy(groupBy)
    }

    private fun dismissSharedDropdown() {
        _uiState.value = _uiState.value.copy(isSharedPopupVisible = false)
    }

    private fun doGroupBy(groupBy: GroupBy) {
        val groupedTransactions = when (groupBy) {
            is GroupBy.Time -> groupByTime(groupBy)
            is GroupBy.Category -> groupByCategory(groupBy)
            is GroupBy.Ledger -> groupByLedger()
        }
        _uiState.value = _uiState.value.copy(groupedTransactions = groupedTransactions)
    }

    private fun fetchTransactions() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val transactions = getAllTransactionsUseCase()
            _uiState.value = _uiState.value.copy(transactions = transactions, isLoading = false)
            // Initial grouping
            _uiState.value.selectedGroupByOption?.let { doGroupBy(it) }
        }
    }

    /**
     * Group transactions by time.
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

        // Sort the groups in descending order of time
        val sortedGroups = groupedMap.entries.sortedByDescending { it.key }

        return sortedGroups.map { (key, value) ->
            GroupedTransaction(
                groupKey = key,
                transactions = value.sortedByDescending { it.transactionDate })
        }
    }

    /**
     * Group transactions by category.
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

        // Sort the groups alphabetically
        val sortedGroups = groupedMap.entries

        return sortedGroups.map { (key, value) ->
            GroupedTransaction(groupKey = key, transactions = value)
        }
    }

    /**
     * Group transactions by ledger.
     */
    private fun groupByLedger(): List<GroupedTransaction> {
        val transactions = _uiState.value.transactions
        val groupedMap = transactions.groupBy { transaction ->
            transaction.ledgerId.toString()
        }
        // Sort the groups alphabetically
        val sortedGroups = groupedMap.entries
        return sortedGroups.map { (key, value) ->
            GroupedTransaction(groupKey = key ?: "Unknown", transactions = value)
        }
    }
}
