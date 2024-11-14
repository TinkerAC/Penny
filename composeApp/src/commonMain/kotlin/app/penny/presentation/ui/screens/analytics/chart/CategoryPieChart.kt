package app.penny.presentation.ui.screens.analytics.chart

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
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import kotlin.random.Random


@Composable
fun CategoryPieChart(


) {

    // 绘制图表
    Surface(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {

        if (false) {
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
            PieChart(
                modifier = Modifier,
                pieChartData = (1..10).map { it ->
                    PieChartData(
                        it.toDouble(),
                        color = MaterialTheme.colorScheme.primary,
                        partName = it.toString(),
                    )
                }

            )
        }

    }
}