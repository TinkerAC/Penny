package app.penny.presentation.ui.screens.analytics.chartAndTable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType

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
        label = "收入",
        lineType = LineType.CURVED_LINE,
        lineShadow = true
    )

    val expenseLine = LineParameters(
        data = expenseValues,
        lineColor = Color(0xFFF44336), // 红色
        label = "支出",
        lineType = LineType.CURVED_LINE,
        lineShadow = true
    )

    // 绘制图表
    Surface(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {

        if (incomeValues.isEmpty() && expenseValues.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                )
            }
        } else {
            LineChart(
                modifier = modifier.padding(16.dp),
                linesParameters = listOf(incomeLine, expenseLine),
                xAxisData = xAxisData,
                legendPosition = LegendPosition.TOP,
                isGrid = true,
                gridColor = Color.LightGray,
                yAxisStyle = TextStyle(color = Color.Black),
                xAxisStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 12.sp
                ),
                descriptionStyle = TextStyle(color = Color.Black),
                showGridWithSpacer = true,
            )


        }

    }
}