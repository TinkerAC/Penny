package app.penny.feature.transactions

import kotlinx.datetime.LocalDate

/**
 * 用于处理各种用户意图的sealed class
 */
sealed class TransactionIntent {
    data class ShowSharedDropdown(val groupByType: GroupByType) : TransactionIntent()
    data class SelectGroupByOption(val groupBy: GroupBy) : TransactionIntent()
    object DismissSharedDropdown : TransactionIntent()
    object ToggleView : TransactionIntent()  // 切换视图（列表/日历）
    data class SelectDate(val date: LocalDate) : TransactionIntent()  // 选择日期
}