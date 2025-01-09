package app.penny.servershared.dto

import app.penny.presentation.enumerate.Language
import app.penny.core.domain.model.valueObject.YearMonth
import kotlinx.serialization.Serializable


@Serializable
data class MonthlyReportData(
    val yearMonth: YearMonth, // Year and month of the report
    val totalIncome: Double, // Total income for the month
    val totalExpense: Double, // Total expenses for the month
    val totalBalance:Double, // Total balance for the month
    val incomeCategories: List<CategoryData>, // Income breakdown
    val expenseCategories: List<CategoryData>, // Expense breakdown
    val averageExpensePerDay : Double, // Average expense per day
    val largestExpense: LargestExpense, // Largest single expense
    val language: Language // Language for the report
)

@Serializable
data class CategoryData(
    val category: String, // Category name (e.g., Salary, Food)
    val amount: Double, // Amount for the category
    val percentage: Double // Percentage of the total
)

@Serializable
data class DailyExpense(
    val date: String, // Date of the expense (yyyy-MM-dd format)
    val amount: Double // Expense amount for the day
)

@Serializable
data class LargestExpense(
    val category: String, // Category of the largest expense
    val amount: Double, // Amount of the largest expense
    val description: String // Description of the expense
)


