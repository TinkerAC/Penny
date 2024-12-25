// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionUiState.kt
package app.penny.feature.transactions

import app.penny.core.domain.model.GroupIdentifier
import app.penny.core.domain.model.Summary
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import app.penny.shared.SharedRes
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import dev.icerock.moko.resources.StringResource
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

sealed class GroupByType(
    val displayName: StringResource
) {
    abstract val options: List<GroupOption>

    data object Time : GroupByType(SharedRes.strings.group_by_time) {
        sealed class GroupOption(
            displayText:
            StringResource
        ) : GroupByType.GroupOption(displayText) {

            data object Day : GroupOption(SharedRes.strings.group_by_day)
            data object Week : GroupOption(SharedRes.strings.group_by_week)
            data object Month : GroupOption(SharedRes.strings.group_by_month)
            data object Season : GroupOption(SharedRes.strings.group_by_season)
            data object Year : GroupOption(SharedRes.strings.group_by_year)

            companion object {
                val items = listOf(Day, Week, Month, Season, Year)
            }
        }

        override val options: List<GroupOption> = GroupOption.items
    }

    data object Category : GroupByType(SharedRes.strings.group_by_category) {
        sealed class GroupOption(displayText: StringResource) :
            GroupByType.GroupOption(displayText) {
            data object Primary : GroupOption(SharedRes.strings.group_by_primary_category)
            data object Secondary : GroupOption(SharedRes.strings.group_by_secondary_category)

            companion object {
                val items = listOf(Primary, Secondary)
            }
        }

        override val options: List<GroupOption> = GroupOption.items
    }

    //base GroupOption class, inherited by GroupOption of each GroupByType
    sealed class GroupOption(val displayText: StringResource)

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