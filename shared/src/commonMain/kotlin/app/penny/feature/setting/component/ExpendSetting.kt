package app.penny.feature.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun <T> ExpendSetting(
    settingName: String,
    currentValue: T,
    options: List<T>,
    onValueChange: (T) -> Unit,
    optionContent: @Composable (T, Boolean, Modifier) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // 折叠状态下的大小较小，展开状态下较大
    val collapsedSize = 36.dp
    val expandedSize = 72.dp

    Column {
        // 当前选中的值行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = settingName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // 折叠状态时显示较小的OptionContent
                optionContent(
                    currentValue,
                    true,
                    Modifier.size(collapsedSize)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 展开/收起符号
                Icon(
                    imageVector = if (expanded)
                        androidx.compose.material.icons.Icons.Filled.ArrowDropUp
                    else
                        androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 选项列表
        if (expanded) {
            // 展开时使用 LazyRow 横向排列，OptionContent 较大
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                items(options) { option ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onValueChange(option)
                                expanded = false // 选择后收起
                            }
                    ) {
                        optionContent(option, option == currentValue, Modifier.size(expandedSize))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeColorOptionContent(
    appTheme: AppTheme,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    // 始终为带有圆角的正方形，通过aspectRatio(1f)确保正方形比例

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            )
            .padding(6.dp), // 内边距稍小，让内容有空间
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 色块，依然使用正方形且有圆角
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    appTheme.primaryColor,
                    shape = RoundedCornerShape(4.dp)
                )

        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(appTheme.nameStringResource),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

