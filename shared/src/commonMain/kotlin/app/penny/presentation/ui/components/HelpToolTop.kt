package app.penny.presentation.ui.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun HelpTooltip(helpText: String, enabled: Boolean = true) {
    var showTooltip by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { if (enabled) showTooltip = !showTooltip },
            modifier = Modifier.size(20.dp), // 调整图标按钮的大小
            enabled = enabled // 根据传入的 enabled 状态设置 IconButton 的可用性
        ) {
            Icon(
                imageVector = Icons.Outlined.Help,
                contentDescription = "Help",
                tint = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.4f
                )
            )
        }

        if (showTooltip) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, -40), // 根据需要调整 Popup 的位置
                onDismissRequest = { showTooltip = false },
                properties = PopupProperties(focusable = true)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = helpText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
