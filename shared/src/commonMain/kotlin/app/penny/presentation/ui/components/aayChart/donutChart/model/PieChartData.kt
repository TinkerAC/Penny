package app.penny.presentation.ui.components.aayChart.donutChart.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.StringResource

data class PieChartData(
    val data:Double,
    val color: Color,
    val partName:String
)
