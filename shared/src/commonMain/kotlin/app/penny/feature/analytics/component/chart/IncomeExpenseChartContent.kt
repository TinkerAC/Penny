// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/chartAndTable/IncomeExpenseChartContent.kt
package app.penny.feature.analytics.component.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import app.penny.feature.analytics.IncomeExpenseTrendChartData
import app.penny.presentation.ui.components.aayChart.baseComponents.model.LegendPosition
import app.penny.presentation.ui.components.aayChart.lineChart.LineChart
import app.penny.presentation.ui.components.aayChart.lineChart.model.LineParameters
import app.penny.presentation.ui.components.aayChart.lineChart.model.LineType
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun IncomeExpenseChartContent(
    incomeExpenseTrendChartData: IncomeExpenseTrendChartData,
) {
    // 创建 LineParameters 列表
    val incomeLine = LineParameters(
        data = incomeExpenseTrendChartData.incomeValues,
        lineColor = Color(0xFF4CAF50), // 绿色
        label =
        stringResource(SharedRes.strings.income),
        lineType = LineType.CURVED_LINE,
        lineShadow = true
    )

    val expenseLine = LineParameters(
        data = incomeExpenseTrendChartData.expenseValues,
        lineColor = Color(0xFFF44336), // 红色
        label = stringResource(SharedRes.strings.expense),
        lineType = LineType.CURVED_LINE,
        lineShadow = true
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        linesParameters = listOf(incomeLine, expenseLine),
        xAxisData = incomeExpenseTrendChartData.xAxisData,
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


