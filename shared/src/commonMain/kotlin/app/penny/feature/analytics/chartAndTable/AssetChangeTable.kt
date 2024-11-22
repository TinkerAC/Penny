package app.penny.feature.analytics.chartAndTable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate

@Composable
fun AssetChangeTable(
    modifier: Modifier = Modifier,
    assetChangeData: List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>>
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
        ) {
            // 标题
            Text(
                text = "资产日变动表",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            // 表头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 8.dp)
                //
            ) {
                TableCell(text = "日期", weight = 1f, isHeader = true)
                TableCell(text = "收入", weight = 1f, isHeader = true)
                TableCell(text = "支出", weight = 1f, isHeader = true)
                TableCell(text = "结余", weight = 1f, isHeader = true)
            }

            // 表格内容
            assetChangeData.forEachIndexed { index, (date, dailyChange) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.background
                        )
                        .padding(vertical = 8.dp)
                ) {
                    TableCell(text = date.toString(), weight = 1f)
                    TableCell(text = dailyChange.first.toPlainString(), weight = 1f)
                    TableCell(text = dailyChange.second.toPlainString(), weight = 1f)
                    TableCell(text = dailyChange.third.toPlainString(), weight = 1f)
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false
) {
    Text(
        text = text,
        style = if (isHeader)
            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        else
            MaterialTheme.typography.bodyMedium,
        color = if (isHeader)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
//            .weight(weight)
            .padding(horizontal = 8.dp)
    )
}