// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/chartAndTable/CategoryPieChart.kt
package app.penny.feature.analytics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.SliderToggleButton
import app.penny.presentation.ui.components.aayChart.baseComponents.model.LegendPosition
import app.penny.presentation.ui.components.aayChart.donutChart.DonutChart
import app.penny.presentation.ui.components.aayChart.donutChart.model.PieChartData
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 切换收入和支出
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SliderToggleButton(
                    options = listOf(
                        stringResource(SharedRes.strings.income),
                        stringResource(SharedRes.strings.expense)
                    ),
                    selectedIndex = if (isIncome) 0 else 1,
                    onToggle = {
                        isIncome = it == 0
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            DonutChart(
                modifier = Modifier
                    .width(400.dp)
                    .height(300.dp)
                    .padding(8.dp),
                pieChartData = if (isIncome) incomePieChartData else expensePieChartData,
                legendPosition = LegendPosition.BOTTOM,
                centerTitle = if (isIncome) stringResource(SharedRes.strings.income) else stringResource(
                    SharedRes.strings.expense
                )


            )
        }

    }
}

