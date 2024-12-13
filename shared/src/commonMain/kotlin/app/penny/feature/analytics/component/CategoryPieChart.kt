package app.penny.feature.analytics.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
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
    var chartType by remember { mutableStateOf(0) }

    // Surface容器包裹 PieChart
    Surface(
        modifier = modifier
            .height(300.dp)
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // 切换收入和支出
            Switch(
                checked = chartType == 0,
                onCheckedChange = { isChecked ->
                    chartType = if (isChecked) 0 else 1
                    Logger.d("chartType: $chartType")
                },
                modifier = Modifier.padding(16.dp)
            )

            // 根据 chartType 显示不同的 PieChart 数据
            PieChart(
                modifier = Modifier.fillMaxWidth(),
                pieChartData = if (chartType == 0) expensePieChartData else incomePieChartData,
                legendPosition = LegendPosition.TOP
            )
        }
    }
}
