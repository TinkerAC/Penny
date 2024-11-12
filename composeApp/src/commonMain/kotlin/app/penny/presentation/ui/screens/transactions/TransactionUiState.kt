package app.penny.presentation.ui.screens.transactions

import app.penny.domain.model.TransactionModel


data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionModel> = emptyList(),
    val errorMessage: String = "",
    val selectedGroupByType: GroupByType? = null,
    val selectedGroupByOption: GroupBy? = null,
    val isSharedPopupVisible: Boolean = false,
    val groupByOptions: List<GroupBy> = GroupByType.getGroupByOptions(GroupByType.Time),
    val groupedTransactions: List<GroupedTransaction> = emptyList()
)

sealed class GroupBy(val displayText: String) {

    // Time category and its subcategories
    sealed class Time(displayText: String) : GroupBy(displayText) {
        object Day : Time("Day")
        object Week : Time("Week")
        object Month : Time("Month")
        object Season : Time("Season")
        object Year : Time("Year")

        companion object {
            val items = listOf(Day, Week, Month, Season, Year)
        }
    }

    // Category and its subcategories
    sealed class Category(displayText: String) : GroupBy(displayText) {
        object Level1 : Category("Level 1")
        object Level2 : Category("Level 2")

        companion object {
            val items = listOf(Level1, Level2)
        }
    }

    // Other independent categories
    object Ledger : GroupBy("Ledger")
}

sealed class GroupByType(val displayName: String) {
    object Time : GroupByType("Time")
    object Category : GroupByType("Category")
    object Ledger : GroupByType("Ledger")

    companion object {
        val items: List<GroupByType> = listOf(Time, Category, Ledger)

        fun getGroupByOptions(groupByType: GroupByType): List<GroupBy> {
            return when (groupByType) {
                Time -> GroupBy.Time.items
                Category -> GroupBy.Category.items
                Ledger -> emptyList()
            }
        }
    }
}


data class GroupedTransaction(
    val groupKey: String,
    val transactions: List<TransactionModel>
)