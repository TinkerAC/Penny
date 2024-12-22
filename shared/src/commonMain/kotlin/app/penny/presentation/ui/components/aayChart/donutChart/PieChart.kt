package app.penny.presentation.ui.components.aayChart.donutChart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.presentation.ui.components.aayChart.baseComponents.model.LegendPosition
import app.penny.presentation.ui.components.aayChart.donutChart.component.PieChartDescriptionComposable
import app.penny.presentation.ui.components.aayChart.donutChart.component.draPieCircle
import app.penny.presentation.ui.components.aayChart.donutChart.component.drawPedigreeChart
import app.penny.presentation.ui.components.aayChart.donutChart.model.ChartTypes
import app.penny.presentation.ui.components.aayChart.donutChart.model.PieChartData
import app.penny.presentation.ui.components.aayChart.utils.ChartDefaultValues
import app.penny.presentation.ui.components.aayChart.utils.checkIfDataIsNegative
import kotlin.math.min


/**
 * Composable function to render a pie chart with an optional legend.
 *
 * @param modifier Modifier for configuring the layout and appearance of the pie chart.
 * @param pieChartData List of data for the pie chart, including labels and values.
 * @param animation Animation specification for the pie chart transitions (默认3秒线性动画).
 * @param textRatioStyle TextStyle for ratio text labels (默认 fontSize = 12sp).
 * @param outerCircularColor Color of the outer circular border (默认 Gray).
 * @param ratioLineColor Color of the lines connecting ratio labels to chart segments (默认 Gray).
 * @param descriptionStyle TextStyle for the legend text.
 * @param legendPosition Position of the legend (默认 LegendPosition.TOP) - 新增 RATIO_SIDE。
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    pieChartData: List<PieChartData>,
    animation: AnimationSpec<Float> = TweenSpec(durationMillis = 3000),
    textRatioStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
    outerCircularColor: Color = Color.Gray,
    ratioLineColor: Color = Color.Gray,
    descriptionStyle: TextStyle = TextStyle.Default,
    legendPosition: LegendPosition = ChartDefaultValues.legendPosition, // 可能是 TOP, BOTTOM, DISAPPEAR, RATIO_SIDE
) {
    // 计算每段比例 + 总和
    var totalSum = 0.0f
    pieChartData.forEach {
        totalSum += it.data.toFloat()
    }
    val pieValueWithRatio = pieChartData.map { 360 * it.data.toFloat() / totalSum }.toMutableList()

    val textMeasure = rememberTextMeasurer()

    // 数据检查
    checkIfDataIsNegative(data = pieChartData.map { it.data })

    // 动画
    val transitionProgress = remember(pieValueWithRatio) { Animatable(0F) }
    LaunchedEffect(pieChartData) {
        transitionProgress.animateTo(1F, animationSpec = animation)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        when (legendPosition) {
            LegendPosition.TOP -> {
                // 图例在上方
                PieChartDescriptionComposable(
                    pieChartData = pieChartData,
                    descriptionStyle = descriptionStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                )
                drawPieChart(
                    modifier = Modifier.weight(1.5f),
                    pieChartData = pieChartData,
                    textRatioStyle = textRatioStyle,
                    outerCircularColor = outerCircularColor,
                    ratioLineColor = ratioLineColor,
                    pieValueWithRatio = pieValueWithRatio,
                    totalSum = totalSum,
                    transitionProgress = transitionProgress,
                    textMeasure = textMeasure,
                    showSideLegend = false // 该参数后面解释
                )
            }

            LegendPosition.BOTTOM -> {
                // 图例在下方
                drawPieChart(
                    modifier = Modifier.weight(1.5f),
                    pieChartData = pieChartData,
                    textRatioStyle = textRatioStyle,
                    outerCircularColor = outerCircularColor,
                    ratioLineColor = ratioLineColor,
                    pieValueWithRatio = pieValueWithRatio,
                    totalSum = totalSum,
                    transitionProgress = transitionProgress,
                    textMeasure = textMeasure,
                    showSideLegend = false
                )

                PieChartDescriptionComposable(
                    pieChartData = pieChartData,
                    descriptionStyle = descriptionStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                )
            }

            LegendPosition.DISAPPEAR -> {
                // 不显示图例
                drawPieChart(
                    modifier = Modifier.weight(1.5f),
                    pieChartData = pieChartData,
                    textRatioStyle = textRatioStyle,
                    outerCircularColor = outerCircularColor,
                    ratioLineColor = ratioLineColor,
                    pieValueWithRatio = pieValueWithRatio,
                    totalSum = totalSum,
                    transitionProgress = transitionProgress,
                    textMeasure = textMeasure,
                    showSideLegend = false
                )
            }

            LegendPosition.RATIO_SIDE -> {
                drawPieChart(
                    modifier = Modifier
                        .weight(1f), // 你可根据UI需求调整
                    pieChartData = pieChartData,
                    textRatioStyle = textRatioStyle,
                    outerCircularColor = outerCircularColor,
                    ratioLineColor = ratioLineColor,
                    pieValueWithRatio = pieValueWithRatio,
                    totalSum = totalSum,
                    transitionProgress = transitionProgress,
                    textMeasure = textMeasure,
                    showSideLegend = true // 开启边上图例模式
                )
            }
        }
    }
}


@Composable
private fun drawPieChart(
    modifier: Modifier = Modifier,
    pieChartData: List<PieChartData>,
    textRatioStyle: TextStyle,
    outerCircularColor: Color,
    ratioLineColor: Color,
    pieValueWithRatio: MutableList<Float>,
    totalSum: Float,
    transitionProgress: Animatable<Float, AnimationVector1D>,
    textMeasure: TextMeasurer,
    showSideLegend: Boolean // 新增参数，是否把图例显示在百分比旁边
) {
    val labelColor  = MaterialTheme.colorScheme.onSurface
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val minValue = min(canvasWidth, canvasHeight)
                    .coerceAtMost(canvasHeight / 2)
                    .coerceAtMost(canvasWidth / 2)
                val arcWidth = (size.minDimension.dp.toPx() * 0.13f).coerceAtMost(minValue / 4)

                // 主体：绘制饼图(内含比例文字)
                drawPedigreeChart(
                    pieValueWithRatio = pieValueWithRatio,
                    pieChartData = pieChartData,
                    totalSum = totalSum,
                    transitionProgress = transitionProgress,
                    textMeasure = textMeasure,
                    textRatioStyle = textRatioStyle,
                    ratioLineColor = ratioLineColor,
                    arcWidth = arcWidth,
                    minValue = minValue,
                    pieChart = ChartTypes.PIE_CHART,
                    showSideLegend = showSideLegend,// 传给内部绘制逻辑,
                    labelColor = labelColor
                )

                // 外圈
                draPieCircle(
                    circleColor = outerCircularColor,
                    radiusRatioCircle = (minValue / 2) + (arcWidth / 1.5f)
                )
            }
    )
}