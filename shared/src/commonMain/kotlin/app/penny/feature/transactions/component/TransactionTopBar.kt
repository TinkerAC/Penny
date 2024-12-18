// file: shared/src/commonMain/kotlin/app/penny/feature/transactions/components/TransactionTopBar.kt
package app.penny.feature.transactions.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import app.penny.feature.transactions.GroupByType
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTopBar(
    isCalendarView: Boolean,
    onToggleView: () -> Unit,
    selectedGroupByType: GroupByType?,
    selectedGroupByOption: GroupByType.GroupOption,
    onGroupByOptionSelected: (GroupByType, GroupByType.GroupOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(SharedRes.strings.transaction),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
            },
            actions = {
                Spacer(modifier = Modifier.width(8.dp))
                // 切换日历/列表视图按钮
                IconButton(onClick = onToggleView) {
                    Icon(
                        imageVector = if (isCalendarView) Icons.AutoMirrored.Filled.List else Icons.Rounded.CalendarMonth,
                        contentDescription = stringResource(SharedRes.strings.toggle_view)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ), modifier = Modifier.shadow(elevation = 2.dp)
        )

        // 仅在列表视图中显示 FilterChips
        if (!isCalendarView) {
            GroupFilterChipsRow(
                selectedGroupByType = selectedGroupByType,
                selectedGroupByOption = selectedGroupByOption,
                onGroupByOptionSelected = onGroupByOptionSelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupFilterChipsRow(
    selectedGroupByType: GroupByType?,
    selectedGroupByOption: GroupByType.GroupOption,
    onGroupByOptionSelected: (GroupByType, GroupByType.GroupOption) -> Unit
) {
    // 状态用于跟踪哪个 GroupByType 的下拉菜单是展开的
    var expandedGroupByType by remember { mutableStateOf<GroupByType?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.Start,

    ) {
        GroupByType.items.forEach { groupByType ->
            Box {
                FilterChip(
                    selected = selectedGroupByType == groupByType,
                    onClick = {
                        expandedGroupByType =
                            if (expandedGroupByType == groupByType) null else groupByType
                    },
                    label = {
                        Text(
                            text = if (selectedGroupByType == groupByType) {
                                selectedGroupByOption.displayText
                            } else {
                                groupByType.displayName
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        if (selectedGroupByType == groupByType) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (selectedGroupByType == groupByType)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (selectedGroupByType == groupByType)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // 为当前 GroupByType 添加 DropdownMenu
                DropdownMenu(
                    expanded = expandedGroupByType == groupByType,
                    onDismissRequest = { expandedGroupByType = null }
                ) {
                    groupByType.options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selectedGroupByType == groupByType && selectedGroupByOption == option) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                    Text(text = option.displayText)
                                }
                            },
                            onClick = {
                                onGroupByOptionSelected(groupByType, option)
                                expandedGroupByType = null
                            }
                        )
                    }
                }
            }
        }
    }
}
