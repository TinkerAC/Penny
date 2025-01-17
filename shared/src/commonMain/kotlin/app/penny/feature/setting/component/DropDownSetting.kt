package app.penny.feature.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.FilterChipDropDown

@Composable
fun <T> ExposedDropDownSetting(
    settingName: String,
    enabled: Boolean = true,
    items: List<T>,
    selectedItem: T,
    settingIcon: ImageVector? = null,
    onItemSelected: (T) -> Unit,
    displayMapper: @Composable (T) -> String // Function to get the display text from T
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
            .clickable(onClick = { /* 可选：让整个区域点击也展开菜单，或啥都不做 */ })
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                settingIcon?.let {
                    Icon(
                        imageVector = settingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled) MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.9f
                        ) else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                // 左侧标题
                Text(
                    text = settingName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }


            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChipDropDown(
                    enabled = enabled,
                    items = items,
                    selectedItem = selectedItem,
                    onItemSelected = onItemSelected,
                    displayMapper = displayMapper
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
