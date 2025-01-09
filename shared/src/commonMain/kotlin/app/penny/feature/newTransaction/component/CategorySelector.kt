package app.penny.feature.newTransaction.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.core.domain.enumerate.Category
import app.penny.core.domain.enumerate.Category.Companion.getSubCategories
import app.penny.feature.newTransaction.NewTransactionIntent
import app.penny.feature.newTransaction.NewTransactionUiState
import app.penny.feature.newTransaction.NewTransactionViewModel
import app.penny.getScreenWidthDp
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CategorySelector(
    modifier: Modifier = Modifier,
    primaryCategories: List<Category>,
    viewModel: NewTransactionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // 获取屏幕宽度以动态计算每行的列数
    val screenWidthDp = getScreenWidthDp()
    val minItemSize = 90.dp
    val maxColumns = ((screenWidthDp / minItemSize).toInt()).coerceAtMost(5).coerceAtLeast(1)

    // 将 primaryCategories 分组为多行，并确保每行的列数一致
    val rows = primaryCategories.chunked(maxColumns).map { row ->
        if (row.size < maxColumns) {
            row + List(maxColumns - row.size) { null } // 使用 null 作为填充
        } else {
            row
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // 添加垂直间距，防止重叠
    ) {
        rows.forEachIndexed { rowIndex, rowCategories ->
            // 显示每一行的 PrimaryCategory
            item(key = "row-$rowIndex") {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    rowCategories.forEach { primaryCategory ->
                        if (primaryCategory != null) {
                            PrimaryCategoryItem(
                                category = primaryCategory,
                                isSelected = uiState.selectedParentCategory == primaryCategory,
                                onClick = {
                                    viewModel.handleIntent(
                                        NewTransactionIntent.SelectParentCategory(
                                            primaryCategory
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f) // 使用 aspectRatio 保持正方形，同时自适应宽度
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f)) // 填充空位
                        }
                    }
                }
            }

            // 如果当前行有选中的 PrimaryCategory，则在其下方显示对应的 SecondaryCategory
            val selectedCategoryInRow = rowCategories.find { it == uiState.selectedParentCategory }
            if (selectedCategoryInRow != null) {
                item(key = "secondary-$rowIndex") {
                    SecondaryCategoriesRow(
                        selectedCategory = selectedCategoryInRow,
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun SecondaryCategoriesRow(
    selectedCategory: Category,
    uiState: NewTransactionUiState,
    viewModel: NewTransactionViewModel
) {
    val subCategories = getSubCategories(selectedCategory)
    if (subCategories.isNotEmpty()) {

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.05f))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(selectedCategory.categoryName),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
//
//                HorizontalDivider(
//                    modifier = Modifier.fillMaxWidth()
//                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 70.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp) // 设置最大高度，防止无限扩展
                        .animateContentSize(
                            animationSpec = tween(durationMillis = 300)
                        ),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // 添加水平间距，避免卡片紧密排列
                ) {
                    items(subCategories, key = { it.name }) { subCategory ->
                        SecondaryCategoryItem(
                            category = subCategory,
                            isSelected = uiState.selectedSubCategory == subCategory,
                            onClick = {
                                viewModel.handleIntent(
                                    NewTransactionIntent.SelectSubCategory(
                                        subCategory
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun PrimaryCategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(4.dp) // 添加间距，避免卡片之间紧密排列
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // 让 Column 填满整个 Card 空间
                .padding(12.dp), // 内边距
            horizontalAlignment = Alignment.CenterHorizontally, // 水平居中
            verticalArrangement = Arrangement.Center // 垂直居中
        ) {
            // 图标：大小固定
            Icon(
                imageVector = category.categoryIcon,
                contentDescription = stringResource(category.categoryName),
                modifier = Modifier.size(24.dp), // 图标大小为 24dp
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 动态字体调整：使用字体缩放以适应文本
            Text(
                text = stringResource(category.categoryNameAbbr!!),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 14.sp, // 默认字体大小
                maxLines = 2, // 允许两行显示文本
                textAlign = TextAlign.Center,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth() // 让文本填满宽度，避免溢出
            )
        }
    }
}

@Composable
private fun SecondaryCategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp) // 添加间距，避免卡片之间紧密排列
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(), // 让 Column 填满整个 Card 空间
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 垂直居中
        ) {
            Icon(
                imageVector = category.categoryIcon,
                contentDescription = stringResource(category.categoryName),
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(category.categoryName),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                fontSize = 10.sp, // 调整字体大小以适应
                textAlign = TextAlign.Center,
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth() // 让文本填满宽度，避免溢出
            )
        }
    }
}