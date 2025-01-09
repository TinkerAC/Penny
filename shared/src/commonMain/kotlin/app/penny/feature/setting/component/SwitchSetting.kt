package app.penny.feature.setting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.HelpTooltip

/** 开关设置项 **/
@Composable
fun SwitchSetting(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
    title: String,
    titleTextStyles: TextStyle = MaterialTheme.typography.bodyMedium,
    checked: Boolean,
    helpText: String? = null, // 可选的帮助文本
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true // 是否启用
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = titleTextStyles,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.4f
                )
            )
            if (helpText != null) {
                Spacer(modifier = Modifier.padding(horizontal = 4.dp)) // 增加一些间距
                HelpTooltip(helpText = helpText, enabled = enabled)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = { if (enabled) onCheckedChange(it) },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                disabledCheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                disabledUncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            )
        )
    }
}
