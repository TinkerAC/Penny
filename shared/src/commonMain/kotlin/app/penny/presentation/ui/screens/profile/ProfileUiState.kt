package app.penny.presentation.ui.screens.profile


data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val name: String = "",
    val email: String = "",
    val profileImage: String = "",

    //连续签到天数
    val continuousCheckInDays: Int = 0,
    //记账总笔数
    val totalTransactionCount: Int = 0,
    //记账天数
    val totalTransactionDays: Int = 0,

    ) {


}