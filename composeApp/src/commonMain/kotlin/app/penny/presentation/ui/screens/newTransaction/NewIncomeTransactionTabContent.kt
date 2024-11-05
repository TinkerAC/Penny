// NewIncomeTransactionTabContent.kt

package app.penny.presentation.ui.screens.newTransaction

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.penny.domain.enum.IncomeCategory
import app.penny.presentation.ui.components.CategorySelector
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewIncomeTransactionTabContent(
    modifier: Modifier = Modifier,
    onCategorySelected: (IncomeCategory) -> Unit
) {
//    val parentCategories = IncomeCategory.getAllParentCategories()
//    CategorySelector(
//        modifier = modifier,
//        parentCategories = parentCategories,
//        getSubCategories = { parent -> IncomeCategory.getSubCategories(parent) },
//        getCategoryName = { category -> category.categoryName },
//        getCategoryIcon = { category -> painterResource(category.categoryIcon) },
//        onCategorySelected = onCategorySelected
//    )
}