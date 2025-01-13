package app.penny.feature.loanCalculator

import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource

data class LoanCalculatorUiState(
    val loanAmount: String = "",
    val interestRate: String = "",
    val loanYears: Int = 5,
    val repaymentMethod: RepaymentMethod = RepaymentMethod.EqualPrincipalAndInterest,
    val monthlyPayment: String = "",
    val totalInterest: String = "",
    val totalAmount: String = "",
    val errorMessage: String? = null
)

enum class RepaymentMethod(
    displayText: StringResource,
) {
    EqualPrincipalAndInterest(SharedRes.strings.equal_principal_and_interest), // 等额本息
    EqualPrincipal(SharedRes.strings.equal_principal), // 等额本金
}
