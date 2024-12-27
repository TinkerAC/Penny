package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.utils.getDaysInMonth
import app.penny.core.utils.localDateNow
import app.penny.servershared.dto.CategoryData
import app.penny.servershared.dto.LargestExpense
import app.penny.servershared.dto.MonthlyReportData

class PrepareMonthlyReportDataUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(
        ledger: LedgerModel, yearMonth: YearMonth
    ): MonthlyReportData {
        val allTransactions = transactionRepository.findByLedgerAndYearMonth(
            ledger = ledger, yearMonth = yearMonth
        )


        val allIncomeTransactions = allTransactions.filter {
            it.transactionType == TransactionType.INCOME
        }

        val allExpenseTransactions = allTransactions.filter {
            it.transactionType == TransactionType.EXPENSE
        }

        val totalIncome = allIncomeTransactions.sumOf { it.amount.toString().toDouble() }
        val totalExpense = allExpenseTransactions.sumOf { it.amount.toString().toDouble() }

        val totalBalance = totalIncome - totalExpense


        val incomeCategories = allIncomeTransactions.groupBy { it.category.parentCategory }
            .map { (parentCategory, transactions) ->
                CategoryData(category = parentCategory!!.name,
                    amount = transactions.sumOf { it.amount.toString().toDouble() },
                    percentage = transactions.sumOf {
                        it.amount.toString().toDouble()
                    } / totalIncome)
            }

        val expenseCategories = allExpenseTransactions.groupBy { it.category.parentCategory }
            .map { (parentCategory, transactions) ->
                CategoryData(category = parentCategory!!.name,
                    amount = transactions.sumOf { it.amount.toString().toDouble() },
                    percentage = transactions.sumOf {
                        it.amount.toString().toDouble()
                    } / totalExpense)
            }


        val averageExpensePerDay = totalExpense / getDaysInMonth(yearMonth)
        val largestExpense =
            allExpenseTransactions.maxByOrNull { it.amount.toString().toDouble() }!!.let {
                LargestExpense(
                    category = it.category.parentCategory!!.name,
                    amount = it.amount.toString().toDouble(),
                    description = it.remark ?: ""
                )
            }




        return MonthlyReportData(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            totalBalance = totalBalance,
            incomeCategories = incomeCategories,
            expenseCategories = expenseCategories,
            averageExpensePerDay = averageExpensePerDay,
            largestExpense = largestExpense,
            userLocalDate = localDateNow()
        )
    }
}

