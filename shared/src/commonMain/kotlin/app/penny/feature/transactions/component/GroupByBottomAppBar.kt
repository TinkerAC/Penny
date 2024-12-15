package app.penny.feature.transactions.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.feature.transactions.GroupByType
import app.penny.feature.transactions.TransactionIntent
import app.penny.feature.transactions.TransactionUiState
import app.penny.feature.transactions.TransactionViewModel

@Composable
fun GroupByBottomAppBar(
    uiState: TransactionUiState,
    viewModel: TransactionViewModel,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // 标题
            Text(
                text = "选择分组方式",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            // 分组类型和分组选项的FilterChips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 分组类型的FilterChips
                items(GroupByType.items) { groupByType ->
                    val isSelected = uiState.selectedGroupByType == groupByType
                    val displayText = if (isSelected)
                        uiState.selectedGroupByOption?.displayText ?: groupByType.displayName
                    else
                        groupByType.displayName

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.handleIntent(TransactionIntent.ShowSharedDropdown(groupByType))
                        },
                        label = {
                            Text(
                                text = displayText,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                // 分组选项的FilterChips（如果有的话）
                if (uiState.groupByOptions.isNotEmpty()) {
                    items(uiState.groupByOptions) { groupByOption ->
                        val isSelected = uiState.selectedGroupByOption == groupByOption
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.handleIntent(
                                    TransactionIntent.SelectGroupByOption(groupByOption)
                                )
                            },
                            label = {
                                Text(
                                    text = groupByOption.displayText,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            shape = MaterialTheme.shapes.extraSmall,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}
