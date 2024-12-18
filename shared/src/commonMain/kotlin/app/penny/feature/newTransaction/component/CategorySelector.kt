package app.penny.feature.newTransaction.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.Category.Companion.getSubCategories
import app.penny.feature.newTransaction.NewTransactionIntent
import app.penny.feature.newTransaction.NewTransactionViewModel
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CategorySelector(
    modifier: Modifier = Modifier,
    primaryCategories: List<Category>,
    viewModel: NewTransactionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 父分类网格
        LazyVerticalGrid(
            columns = GridCells.FixedSize(80.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(primaryCategories, key = { it.name }) { primaryCategory ->
                PrimaryCategoryItem(
                    category = primaryCategory,
                    isSelected = uiState.selectedParentCategory == primaryCategory,
                    onClick = {
                        viewModel.handleIntent(
                            NewTransactionIntent.SelectParentCategory(
                                primaryCategory
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 子分类网格，带动画效果
        AnimatedVisibility(
            visible = uiState.selectedParentCategory != null,
            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
        ) {
            uiState.selectedParentCategory?.let { parent ->
                val subCategories = getSubCategories(parent)
                if (subCategories.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 80.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
}

@Composable
fun PrimaryCategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // 图标：大小固定
            Icon(
                imageVector = category.categoryIcon,
                contentDescription = stringResource(category.categoryName),
                modifier = Modifier.size(24.dp), // 确保图标大小固定为 24dp
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 动态字体调整：使用字体缩放以适应文本
            Text(
                text = stringResource(category.categoryName),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 14.sp, // 默认字体大小
                maxLines = 2, // 允许两行显示文本
                textAlign = TextAlign.Center,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center)
                    .widthIn(max = 80.dp) // 限制文本最大宽度，避免过长
            )
        }
    }
}


@Composable
fun SecondaryCategoryItem(
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
            .fillMaxWidth()
            .aspectRatio(1f)
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            ).wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

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
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.wrapContentSize(align = Alignment.Center)
            )
        }
    }
}