// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/chartAndTable/IncomeExpenseTrendChartCard.kt
package app.penny.feature.analytics.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.presentation.ui.components.aayChart.baseComponents.model.LegendPosition
import app.penny.presentation.ui.components.aayChart.lineChart.LineChart
import app.penny.presentation.ui.components.aayChart.lineChart.model.LineParameters
import app.penny.presentation.ui.components.aayChart.lineChart.model.LineType
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun IncomeExpenseTrendChartCard(
    modifier: Modifier = Modifier,
    xAxisData: List<String>,
    incomeValues: List<Double>,
    expenseValues: List<Double>
) {
    // 创建 LineParameters 列表
    val incomeLine = LineParameters(
        data = incomeValues,
        lineColor = Color(0xFF4CAF50), // 绿色
        label =
        stringResource(SharedRes.strings.income),
        lineType = LineType.CURVED_LINE,
        lineShadow = true
    )

    val expenseLine = LineParameters(
        data = expenseValues,
        lineColor = Color(0xFFF44336), // 红色
        label = stringResource(SharedRes.strings.expense),
        lineType = LineType.CURVED_LINE,
        lineShadow = true
    )

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
            Text(
                text = stringResource(SharedRes.strings.income_expense_trend),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (incomeValues.isEmpty() && expenseValues.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = stringResource(SharedRes.strings.no_data),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    )
                }
            } else {
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    linesParameters = listOf(incomeLine, expenseLine),
                    xAxisData = xAxisData,
                    legendPosition = LegendPosition.TOP,
                    isGrid = true,
                    animateChart = true,
                    gridColor = Color.LightGray,
                    yAxisStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    xAxisStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                    ),
                    descriptionStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    showGridWithSpacer = true,
                )
            }
        }
    }
}
