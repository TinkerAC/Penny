package app.penny.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun <T> FilterChipDropDown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    displayMapper: @Composable (T) -> String,
    // 新增样式配置
    styleConfig: FilterChipDropDownStyle = defaultFilterChipDropDownStyle(),

) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // 显示FilterChip
        FilterChip(
            selected = styleConfig.alwaysSelected,
            onClick = { expanded = !expanded }, // 点击时切换下拉菜单展开/收起
            label = {
                Text(
                    text = displayMapper(selectedItem),
                    style = styleConfig.labelTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingIcon = {
                if (expanded) {
                    styleConfig.expandedIcon()
                } else {
                    styleConfig.collapsedIcon()
                }
            },
            colors = styleConfig.filterChipColors,
            shape = styleConfig.shape,
            border = styleConfig.border,
        )

        // 下拉菜单
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = styleConfig.dropdownModifier
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = displayMapper(item),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (item == selectedItem)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (item == selectedItem)
                                FontWeight.Bold
                            else
                                FontWeight.Normal
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false // 选中后收起
                    }
                )
            }
        }
    }
}


/**
 *
 * The style configuration for [FilterChipDropDown].
 *
 */
data class FilterChipDropDownStyle(
    val alwaysSelected: Boolean,
    val shape: Shape,
    val border: BorderStroke?,
    val filterChipColors: SelectableChipColors,
    val labelTextStyle: TextStyle,
    val dropdownModifier: Modifier = Modifier,
    val expandedIcon: @Composable () -> Unit? = {
        Icon(
            imageVector = Icons.Default.ArrowDropUp,
            contentDescription = "Collapse"
        )
    },
    val collapsedIcon: @Composable () -> Unit? = {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Expand"
        )
    }
)

@Composable
fun defaultFilterChipDropDownStyle(
    alwaysSelected: Boolean = true,
): FilterChipDropDownStyle {
    return FilterChipDropDownStyle(
        alwaysSelected = alwaysSelected,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        filterChipColors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        ),
        labelTextStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onPrimary
        ),
        dropdownModifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(4.dp)), // 举个例子
        expandedIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropUp,
                contentDescription = "Collapse"
            )
        },
        collapsedIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand"
            )
        }
    )
}
