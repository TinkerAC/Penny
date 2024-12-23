package app.penny.feature.transactions

import kotlinx.datetime.LocalDate

/**
 * 用于处理各种用户意图的sealed class
 */
sealed class TransactionIntent {
    data class SelectGroupByOption(val groupByOption: GroupByType.GroupOption) : TransactionIntent()
    data object ToggleView : TransactionIntent()  // 切换视图（列表/日历）
    data class SelectDate(val date: LocalDate) : TransactionIntent()  // 选择日期
}