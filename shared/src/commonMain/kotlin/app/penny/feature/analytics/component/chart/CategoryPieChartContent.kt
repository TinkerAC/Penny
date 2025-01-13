// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/chartAndTable/CategoryPieChart.kt
package app.penny.feature.analytics.component.chart

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
fun CategoryPieChartContent(
    incomePieChartData: List<PieChartData>,
    expensePieChartData: List<PieChartData>,
    isExpense: Boolean,
) {

    DonutChart(
        modifier = Modifier
            .width(400.dp)
            .height(300.dp)
            .padding(8.dp),
        pieChartData = if (isExpense) expensePieChartData else incomePieChartData,
        legendPosition = LegendPosition.BOTTOM,
        centerTitle = stringResource(
            if (isExpense) SharedRes.strings.expense else SharedRes.strings.income
        )


    )
}



