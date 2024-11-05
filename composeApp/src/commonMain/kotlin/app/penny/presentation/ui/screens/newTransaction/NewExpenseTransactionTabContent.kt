// NewExpenseTransactionTabContent.kt

package app.penny.presentation.ui.screens.newTransaction

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.penny.domain.enum.ExpenseCategory
import app.penny.presentation.ui.components.CategorySelector

@Composable
fun NewExpenseTransactionTabContent(
    modifier: Modifier = Modifier,
    onCategorySelected: (ExpenseCategory) -> Unit
) {
    val parentCategories = ExpenseCategory.getAllParentCategories()
    CategorySelector(
        modifier = modifier,
        parentCategories = parentCategories,
        getSubCategories = { parent -> ExpenseCategory.getSubCategories(parent) },
        getCategoryName = { category -> category.categoryName },
//        getCategoryIcon = { category -> category.categoryIcon },
        onCategorySelected = onCategorySelected
    )
}


