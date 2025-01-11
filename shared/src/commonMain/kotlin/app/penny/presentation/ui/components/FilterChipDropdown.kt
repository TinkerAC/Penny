package app.penny.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
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
    enabled: Boolean = true,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    displayMapper: @Composable (T) -> String,
    styleConfig: FilterChipDropDownStyle = defaultFilterChipDropDownStyle(enabled = enabled),
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // 显示FilterChip
        FilterChip(
            selected = styleConfig.alwaysSelected,
            onClick = {
                if (enabled) {
                    expanded = !expanded
                }
            },
            label = {
                Text(
                    text = displayMapper(selectedItem),
                    style = styleConfig.labelTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled) else MaterialTheme.colorScheme.onSurface
                )

            },
            colors = styleConfig.filterChipColors,
            shape = styleConfig.shape,
            border = styleConfig.border,
            enabled = enabled

        )

        // 下拉菜单
        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
            modifier = styleConfig.dropdownModifier
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = displayMapper(item),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (item == selectedItem)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (item == selectedItem)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal
                            )
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false // 选中后收起
                    },
                    enabled = enabled
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
    enabled: Boolean = true
): FilterChipDropDownStyle {
    return FilterChipDropDownStyle(
        alwaysSelected = alwaysSelected,
        shape = RoundedCornerShape(12.dp),
        border =
        BorderStroke(
            2.dp,
            if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = ContentAlpha.disabled
            )
        ),
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
