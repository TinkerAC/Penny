package app.penny.feature.transactions

import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import kotlinx.datetime.LocalDate

data class TransactionUiState(
    val user:UserModel? = null,
    val isLoading: Boolean = false,
    val transactions: List<TransactionModel> = emptyList(),
    val errorMessage: String = "",
    val selectedGroupByType: GroupByType? = null,
    val selectedGroupByOption: GroupBy? = null,
    val sharedPopUpVisible: Boolean = false,
    val groupByOptions: List<GroupBy> = GroupByType.getGroupByOptions(GroupByType.Time),
    val groupedTransactions: List<GroupedTransaction> = emptyList(),
    val isCalendarView: Boolean = false,  // 新增，标记当前是否为日历视图
    val selectedDate: LocalDate? = null   // 新增，选中的日期
)

sealed class GroupBy(val displayText: String) {

    // 时间类别及其子类别
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

    // 分类类别及其子类别
    sealed class Category(displayText: String) : GroupBy(displayText) {
        object Level1 : Category("Level 1")
        object Level2 : Category("Level 2")

        companion object {
            val items = listOf(Level1, Level2)
        }
    }

    // 其他独立类别
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