package app.penny.feature.myLedger.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.core.domain.model.LedgerModel
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun LedgerCard(
    ledgerModel: LedgerModel
) {
    Card(
        shape = RoundedCornerShape(12.dp), // 更现代的圆角
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .padding(24.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) // 添加 2dp 的主色调描边
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // 添加内边距以更美观
            horizontalArrangement = Arrangement.spacedBy(12.dp) // 元素间距
        ) {
            // 左侧图片
            Image(
                painter = painterResource(ledgerModel.cover.drawable),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp) // 更大的图片展示
                    .aspectRatio(1f)
            )

            // 右侧内容
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 顶部标题行
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = ledgerModel.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { /* TODO: edit ledger */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary // 主色调图标
                        )
                    }
                }

                // 文件余额和条目数量
                Text(
                    text = "Balance: ${ledgerModel.balance}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Entries: ${ledgerModel.count}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
