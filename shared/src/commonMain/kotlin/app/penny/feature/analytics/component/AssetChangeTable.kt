// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/chartAndTable/AssetChangeTable.kt
package app.penny.feature.analytics.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.shared.SharedRes
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate

@Composable
fun AssetChangeTable(
    modifier: Modifier = Modifier,
    assetChangeData: List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>>
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题
            Text(
                text =
                stringResource(SharedRes.strings.asset_daily_change),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 表头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 8.dp)
            ) {

                TableCell(
                    text = stringResource(SharedRes.strings.date),
                    modifier = Modifier.weight(1f), isHeader = true
                )
                TableCell(
                    text = stringResource(SharedRes.strings.income),
                    modifier = Modifier.weight(1f), isHeader = true
                )
                TableCell(
                    text = stringResource(SharedRes.strings.expense),
                    modifier = Modifier.weight(1f), isHeader = true
                )
                TableCell(
                    text = stringResource(SharedRes.strings.balance),
                    modifier = Modifier.weight(1f), isHeader = true
                )
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
                    TableCell(text = date.toString(), modifier = Modifier.weight(1f))
                    TableCell(
                        text = dailyChange.first.toPlainString(),
                        modifier = Modifier.weight(1f)
                    )
                    TableCell(
                        text = dailyChange.second.toPlainString(),
                        modifier = Modifier.weight(1f)
                    )
                    TableCell(
                        text = dailyChange.third.toPlainString(),
                        modifier = Modifier.weight(1f),
                        isBalance = true
                    )
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    isBalance: Boolean = false,
    modifier: Modifier = Modifier,
    isHeader: Boolean = false
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = text,
            style = if (isHeader)
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            else
                MaterialTheme.typography.bodyMedium,
            color = if (isBalance) {
                if (text.toBigDecimal() >= BigDecimal.ZERO)
                // 收入
                    Color(0xFF4CAF50)//TODO: use MaterialTheme
                else
                // 支出
                    Color(0xFFF44336)
            } else {

                if (isHeader)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
    }

}