// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/chartAndTable/CategoryPieChart.kt
package app.penny.feature.analytics.chartAndTable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData

@Composable
fun CategoryPieChart(
    modifier: Modifier = Modifier,
    incomePieChartData: List<PieChartData>,
    expensePieChartData: List<PieChartData>
) {
    // 使用 remember 和 mutableStateOf 来存储图表类型
    var isIncome by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 切换收入和支出
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "显示: ",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                SegmentedButton(
                    selected = isIncome,
                    onSelected = { isIncome = true },
                    text = "收入"
                )
                Spacer(modifier = Modifier.width(8.dp))
                SegmentedButton(
                    selected = !isIncome,
                    onSelected = { isIncome = false },
                    text = "支出"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 根据图表类型显示不同的 PieChart 数据
            PieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                pieChartData = if (isIncome) incomePieChartData else expensePieChartData,
                legendPosition = LegendPosition.BOTTOM
            )
        }
    }
}

@Composable
fun SegmentedButton(selected: Boolean, onSelected: () -> Unit, text: String) {
    Button(
        onClick = onSelected,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.width(100.dp)
    ) {
        Text(text)
    }
}
