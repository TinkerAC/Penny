// CategorySelector.kt

package app.penny.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.domain.enum.Category
import app.penny.presentation.ui.screens.newTransaction.NewTransactionIntent
import app.penny.presentation.ui.screens.newTransaction.NewTransactionViewModel

@Composable
fun CategorySelector(
    modifier: Modifier = Modifier,
    parentCategories: List<Category>,
    getSubCategories: (Category) -> List<Category>,
    getCategoryName: (Category) -> String,
    viewModel: NewTransactionViewModel
) {

    val groupedParentCategories = parentCategories.chunked(5)
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        groupedParentCategories.forEachIndexed { rowIndex, parentGroup ->
            item(key = "parent_row_$rowIndex") {
                CategoryRowWithSubGrid(
                    parentCategories = parentGroup,
                    selectedParentCategory = uiState.selectedParentCategory,
                    onParentCategoryClick = { parent ->
                        viewModel.handleIntent(NewTransactionIntent.SelectParentCategory(parent))
                    },
                    getSubCategories = getSubCategories,
                    selectedSubCategory = uiState.selectedSubCategory,
                    onSubCategoryClick = { subCategory ->
                        viewModel.handleIntent(NewTransactionIntent.SelectSubCategory(subCategory))
                    },
                    getCategoryName = getCategoryName
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun CategoryRowWithSubGrid(
    parentCategories: List<Category>,
    selectedParentCategory: Category?,
    onParentCategoryClick: (Category) -> Unit,
    getSubCategories: (Category) -> List<Category>,
    selectedSubCategory: Category?,
    onSubCategoryClick: (Category) -> Unit,
    getCategoryName: (Category) -> String
) {
    Column {
        // 父分类行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp), // 固定高度
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            parentCategories.forEach { parent ->
                IconButton(
                    onClick = { onParentCategoryClick(parent) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp) // 外层 padding
                        .background(
                            color = if (selectedParentCategory == parent)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(8.dp) // 内层 padding
                        .fillMaxHeight() // 填满父 Row 的高度
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center, // 垂直居中
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face, // 保持 Icon 不变
                            contentDescription = getCategoryName(parent),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = getCategoryName(parent),
                            fontSize = 15.sp, // 字体大小
                            //加粗
                            fontWeight = FontWeight.Bold

                        )
                    }
                }
            }
        }

        // 子分类网格
        if (selectedParentCategory != null && parentCategories.contains(selectedParentCategory)) {
            val subCategories = getSubCategories(selectedParentCategory)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(0.2f)),

                contentPadding = PaddingValues(4.dp)
            ) {
                items(subCategories, key = { it.hashCode() }) { subCategory ->
                    IconButton(
                        onClick = { onSubCategoryClick(subCategory) },
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                color = if (selectedSubCategory == subCategory)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium
                            ).height(80.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face, // 保持 Icon 不变
                                contentDescription = getCategoryName(subCategory),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = getCategoryName(subCategory), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
