package app.penny.feature.loanCalculator


import app.penny.core.utils.roundToDecimals
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow


class  LoanCalculatorViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(LoanCalculatorUiState())
    val uiState: StateFlow<LoanCalculatorUiState> = _uiState.asStateFlow()


    fun updateLoanAmount(amount: String) {
        _uiState.update { it.copy(loanAmount = amount) }
    }

    fun updateInterestRate(rate: String) {
        _uiState.update {
            it.copy(interestRate = rate)
        }
    }

    fun updateLoanYears(years: Int) {
        _uiState.update {
            it.copy(loanYears = years)
        }
    }

    fun updateRepaymentMethod(method: RepaymentMethod) {
        _uiState.update {
            it.copy(repaymentMethod = method)
        }
    }

    fun calculateLoan() {
        try {
            val amount = _uiState.value.loanAmount.toDoubleOrNull()
                ?: throw IllegalArgumentException("贷款金额无效")
            val rate = _uiState.value.interestRate.toDoubleOrNull()?.div(100)?.div(12)
                ?: throw IllegalArgumentException("利率无效")
            val months = _uiState.value.loanYears * 12

            when (_uiState.value.repaymentMethod) {
                RepaymentMethod.EqualPrincipalAndInterest -> {
                    val monthlyPayment =
                        amount * rate * (1 + rate).pow(months) / ((1 + rate).pow(months) - 1)
                    val totalAmount = monthlyPayment * months
                    _uiState.update {
                        it.copy(
                            monthlyPayment = monthlyPayment.roundToDecimals(2).toString(),
                            totalInterest = (totalAmount - amount).roundToDecimals(2).toString(),
                            totalAmount = totalAmount.roundToDecimals(2).toString(),
                            errorMessage = null
                        )
                    }

                }

                RepaymentMethod.EqualPrincipal -> {
                    val monthlyPrincipal = amount / months
                    var totalInterest = 0.0
                    for (i in 0 until months) {
                        totalInterest += (amount - i * monthlyPrincipal) * rate
                    }
                    _uiState.update {
                        it.copy(
                            monthlyPayment = (monthlyPrincipal + amount * rate).roundToDecimals(2).toString(),
                            totalInterest = totalInterest.toString(),
                            totalAmount = (amount + totalInterest).roundToDecimals(2).toString(),
                            errorMessage = null
                        )
                    }

                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorMessage = e.message)
            }
        }
    }
}
