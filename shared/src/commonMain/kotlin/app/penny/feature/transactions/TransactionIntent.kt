package app.penny.feature.transactions

import kotlinx.datetime.LocalDate

sealed class TransactionIntent {
    data class ShowSharedDropdown(val groupByType: GroupByType) : TransactionIntent()
    data class SelectGroupByOption(val groupBy: GroupBy) : TransactionIntent()
    object DismissSharedDropdown : TransactionIntent()
    object ToggleView : TransactionIntent()  // 新增，用于切换视图
    data class SelectDate(val date: LocalDate) : TransactionIntent()  // 新增，选择日期
}