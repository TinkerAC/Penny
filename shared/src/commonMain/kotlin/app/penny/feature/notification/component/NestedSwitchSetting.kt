package app.penny.feature.notification.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enumerate.Notification
import app.penny.feature.setting.component.SwitchSetting
import dev.icerock.moko.resources.compose.stringResource

/** 通知设置项，包含主开关和多个子设置开关 **/
@Composable
fun NestedSwitchSetting(
    settingName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    childSettings: List<Pair<Notification, Boolean>>, // 子设置名称和开关状态
    onChildCheckedChange: (Notification, Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // 主开关行，包含展开/收起图标
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwitchSetting(
                title = settingName,
                checked = checked,
                onCheckedChange = onCheckedChange,
                helpText = null,// 主开关不需要帮助信息
                titleTextStyles = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )

            )

        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        // 渲染子开关
        childSettings.forEach { (childNotification, childChecked) ->
            SwitchSetting(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 4.dp, bottom = 4.dp),
                title = stringResource(childNotification.displayText),
                checked = childChecked,
                onCheckedChange = { onChildCheckedChange(childNotification, it) },
                enabled = checked, // 根据主开关状态决定是否可用
                helpText = stringResource(childNotification.helpText) // 从 Notification 获取帮助信息
            )
        }

    }
}
