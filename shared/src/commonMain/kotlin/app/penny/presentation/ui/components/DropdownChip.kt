import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> DropdownChip(
    modifier: Modifier = Modifier,
    selectedItem: T?,           // 当前选中的项目
    items: List<T>,             // 可供选择的列表
    onItemSelected: (T) -> Unit,// 用户选中后回调,
    displayMapper: @Composable (T) -> String = { it.toString() }
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(modifier) {
        // 显示当前选中的值
        LabelChip(
            text = displayMapper(selectedItem ?: items.first()),
            modifier = Modifier
        )

        // 右侧“展开/收起”图标
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            modifier = Modifier
                .width(24.dp)
                .then(Modifier) // 你可以继续自定义，比如点击图标也能展开
                .clickable { isExpanded = !isExpanded }
        )
    }

    // 点击Chip的任意位置展开/收起
    LaunchedEffect(isExpanded) {
        // 这里也可以做一些动画或其他处理
    }

    // 下拉菜单
    if (isExpanded) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { isExpanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.toString()) },
                    onClick = {
                        onItemSelected(item)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable

fun LabelChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = modifier
    )
}
