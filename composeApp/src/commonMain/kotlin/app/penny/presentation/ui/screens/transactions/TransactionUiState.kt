package app.penny.presentation.ui.screens.transactions

import app.penny.domain.model.TransactionModel

data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionModel> = emptyList(),
    val errorMessage: String = "",
    val selectedGroupBy: GroupBy = GroupBy.Time.Day

) {


}

//enum class GroupBy {
//    DAY,
//    WEEK,
//    MONTH,
//    SEASON,
//    YEAR,
//    LEVEL1,
//    LEVEL2,
//    LEDGER
//}

sealed class GroupBy(val displayText: String) {

    // Time 类别及其子类别
    sealed class Time(displayText: String) : GroupBy(displayText) {
        data object Day : Time("Day")
        object Week : Time("Week")
        object Month : Time("Month")
        object Season : Time("Season")
        object Year : Time("Year")
    }

    // Category 类别及其子类别
    sealed class Category(displayText: String) : GroupBy(displayText) {
        object Level1 : Category("Level 1")
        object Level2 : Category("Level 2")
    }

    // 其他独立的类别
    object Ledger : GroupBy("Ledger")
}

