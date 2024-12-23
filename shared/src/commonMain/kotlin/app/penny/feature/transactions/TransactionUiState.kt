// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionUiState.kt
package app.penny.feature.transactions

import app.penny.core.domain.model.GroupIdentifier
import app.penny.core.domain.model.Summary
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
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
    val selectedGroupByOption: GroupByType.GroupOption? = null,
    val sharedPopUpVisible: Boolean = false,
    val groupByOptions: List<GroupByType> = GroupByType.items,
    val groupedTransactions: List<GroupedTransaction> = emptyList(),
    val isCalendarView: Boolean = false,
    val selectedDate: LocalDate? = null,
    val calendarViewTransactionOfDate: List<TransactionModel> = emptyList(),
    val calendarViewSummaryByDate: Map<LocalDate, Summary> = emptyMap(),

    // 新增字段
    var totalSummary: Summary = Summary(
        balance = BigDecimal.ZERO,
        income = BigDecimal.ZERO,
        expense = BigDecimal.ZERO
    )


)

sealed class GroupByType(val displayName: String) {
    abstract val options: List<GroupOption>

    data object Time : GroupByType("Time") {
        sealed class GroupOption(displayText: String) : GroupByType.GroupOption(displayText) {

            data object Day : GroupOption("Day")
            data object Week : GroupOption("Week")
            data object Month : GroupOption("Month")
            data object Season : GroupOption("Season")
            data object Year : GroupOption("Year")

            companion object {
                val items = listOf(Day, Week, Month, Season, Year)
            }
        }

        override val options: List<GroupOption> = GroupOption.items
    }

    data object Category : GroupByType("Category") {
        sealed class GroupOption(displayText: String) : GroupByType.GroupOption(displayText) {
            data object Primary : GroupOption("Primary")
            data object Secondary : GroupOption("Secondary")

            companion object {
                val items = listOf(Primary, Secondary)
            }
        }

        override val options: List<GroupOption> = GroupOption.items
    }

    //base GroupOption class, inherited by GroupOption of each GroupByType
    sealed class GroupOption(val displayText: String)

    companion object {
        val items: List<GroupByType> = listOf(Time, Category)
    }
}

data class GroupedTransaction(
    val groupIdentifier: GroupIdentifier,
    val transactions: List<TransactionModel>,
    val summary: Summary
)


sealed class GroupKey {
    data class Time(
        val groupByOption: GroupByType.Time.GroupOption,
    ) : GroupKey()

    data class Category(
        val category: app.penny.core.domain.enum.Category
    ) : GroupKey()
}