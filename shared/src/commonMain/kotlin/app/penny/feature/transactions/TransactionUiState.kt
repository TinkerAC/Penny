// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionUiState.kt
package app.penny.feature.transactions

import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import app.penny.core.domain.model.valueObject.YearMonth
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate

/**
 * UI状态类，包含用户、交易数据、分组信息以及视图选项
 */
data class TransactionUiState(
    val user: UserModel? = null,
    val isLoading: Boolean = false,
    val transactions: List<TransactionModel> = emptyList(),
    val errorMessage: String = "",
    val selectedGroupByType: GroupByType? = null,
    val selectedGroupByOption: GroupBy? = null,
    val sharedPopUpVisible: Boolean = false,
    val groupByOptions: List<GroupBy> = GroupByType.getGroupByOptions(GroupByType.Time),
    val groupedTransactions: List<GroupedTransaction> = emptyList(),
    val isCalendarView: Boolean = false, // 是否为日历视图
    val selectedDate: LocalDate? = null, // 当前选中的日期

    // 新增字段
    val totalIncome:BigDecimal = BigDecimal.ZERO,
    val totalExpense:BigDecimal = BigDecimal.ZERO,
    val totalBalance:BigDecimal = totalIncome - totalExpense

)


sealed class GroupBy(val displayText: String) {
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

    sealed class Category(displayText: String) : GroupBy(displayText) {
        object Level1 : Category("Level 1")
        object Level2 : Category("Level 2")
        companion object {
            val items = listOf(Level1, Level2)
        }
    }

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
    val transactions: List<TransactionModel>,
    val balance: BigDecimal,
    val income: BigDecimal,
    val expense: BigDecimal
){
//    companion object {
//        fun computeSum(transactions: List<TransactionModel>): GroupedTransaction {
//            val balance = transactions.fold(BigDecimal.ZERO) { acc, transaction ->
//                acc + transaction.amount
//            }
//            val income = transactions.filter { it.amount > BigDecimal.ZERO }
//                .fold(BigDecimal.ZERO) { acc, transaction ->
//                    acc + transaction.amount
//                }
//            val expense = transactions.filter { it.amount < BigDecimal.ZERO }
//                .fold(BigDecimal.ZERO) { acc, transaction ->
//                    acc + transaction.amount
//                }
////            return GroupedTransaction(
////                groupKey = transactions.first().timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).toLocalDate().toString(),
////                transactions = transactions,
////                balance = balance,
////                income = income,
////                expense = expense
////            )
//        }
//    }
}
