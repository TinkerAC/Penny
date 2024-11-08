package app.penny.presentation.ui.screens.transactions

sealed class TransactionIntent {
    data class ShowSharedDropdown(val groupByType: GroupByType) : TransactionIntent()
    data class SelectGroupByOption(val groupBy: GroupBy) : TransactionIntent()
    object DismissSharedDropdown : TransactionIntent()
}
