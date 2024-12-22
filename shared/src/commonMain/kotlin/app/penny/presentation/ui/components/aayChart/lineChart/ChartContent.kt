// file: src/commonMain/kotlin/app/penny/presentation/ui/components/aayChart/lineChart/ChartContent.kt
package app.penny.presentation.ui.components.aayChart.lineChart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.aay.aayChart.baseComponents.baseChartContainer
import app.penny.presentation.ui.aay.aayChart.baseComponents.model.GridOrientation
import app.penny.presentation.ui.components.aayChart.lineChart.components.drawDefaultLineWithShadow
import app.penny.presentation.ui.components.aayChart.lineChart.components.drawQuarticLineWithShadow
import app.penny.presentation.ui.components.aayChart.lineChart.model.LineParameters
import app.penny.presentation.ui.components.aayChart.lineChart.model.LineType
import app.penny.presentation.ui.components.aayChart.utils.checkIfDataValid
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun ChartContent(
    modifier: Modifier,
    linesParameters: List<LineParameters>,
    gridColor: Color,
    xAxisData: List<String>,
    isShowGrid: Boolean,
    barWidthPx: Dp,
    animateChart: Boolean,
    showGridWithSpacer: Boolean,
    yAxisStyle: TextStyle,
    xAxisStyle: TextStyle,
    yAxisRange: Int,
    showXAxis: Boolean,
    showYAxis: Boolean,
    specialChart: Boolean,
    onChartClick: (Float, Float) -> Unit,
    clickedPoints: MutableList<Pair<Float, Float>>,
    gridOrientation: GridOrientation,
) {
    val textMeasure = rememberTextMeasurer()

    // 动画进度
    val animatedProgress = remember {
        if (animateChart) Animatable(0f) else Animatable(1f)
    }

    // 动态上下限
    var upperValue by rememberSaveable {
        mutableStateOf(linesParameters.getUpperValue())
    }
    var lowerValue by rememberSaveable {
        mutableStateOf(linesParameters.getLowerValue())
    }

    // 检查数据有效性
    checkIfDataValid(
        xAxisData = xAxisData, linesParameters = linesParameters
    )

    // ---- 新增逻辑：动态计算间距 ----
    val dataPointCount = xAxisData.size.coerceAtLeast(2) // 确保至少有两个数据点

    // 使用 BoxWithConstraints 获取父容器的最大宽度
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val availableWidth = constraints.maxWidth.toFloat()

        // 计算每个数据点之间的间距
        val spacingX = if (dataPointCount > 1) {
            availableWidth / (dataPointCount - 1)
        } else {
            availableWidth
        }

        // 设置一个最小间距，避免数据点过于密集
        val minSpacing = 20.dp.toPx()
        val finalSpacingX = if (spacingX < minSpacing && dataPointCount > 1) {
            minSpacing
        } else {
            spacingX
        }

        // 计算实际绘制的宽度
        val totalChartWidth = if (finalSpacingX == minSpacing && dataPointCount > 1) {
            (dataPointCount - 1) * finalSpacingX
        } else {
            availableWidth
        }

        // 内部的 Canvas 填充这个 Box
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        onChartClick(offset.x, offset.y)
                    }
                }
        ) {
            // 这里的 size.width 就与 finalSpacingX 和 dataPointCount 对应
            val spacingY = (size.height / 8.dp.toPx()).dp

            // xRegionWidth 直接用 finalSpacingX 转换为 Dp
            val xRegionWidth = finalSpacingX.toDp()

            // ----------------- 绘制 grid、背景等 -----------------
            baseChartContainer(
                xAxisData = xAxisData,
                textMeasure = textMeasure,
                upperValue = upperValue.toFloat(),
                lowerValue = lowerValue.toFloat(),
                isShowGrid = isShowGrid,
                backgroundLineWidth = barWidthPx.toPx(),
                gridColor = gridColor,
                showGridWithSpacer = showGridWithSpacer,
                spacingY = spacingY,
                yAxisStyle = yAxisStyle,
                xAxisStyle = xAxisStyle,
                yAxisRange = yAxisRange,
                showXAxis = showXAxis,
                showYAxis = showYAxis,
                specialChart = specialChart,
                isFromBarChart = false,
                gridOrientation = gridOrientation,
                xRegionWidth = xRegionWidth
            )

            // ----------------- 绘制曲线 -----------------
            if (specialChart) {
                if (linesParameters.size >= 2) {
                    throw Exception("Special case must contain just one line")
                }
                linesParameters.forEach { line ->
                    drawQuarticLineWithShadow(
                        line = line,
                        lowerValue = lowerValue.toFloat(),
                        upperValue = upperValue.toFloat(),
                        animatedProgress = animatedProgress,
                        spacingX = finalSpacingX.dp,
                        spacingY = spacingY,
                        specialChart = specialChart,
                        clickedPoints = clickedPoints,
                        xRegionWidth = xRegionWidth,
                        textMeasurer = textMeasure
                    )
                }
            } else {
                if (linesParameters.size >= 2) {
                    // 多条折线时，点击坐标只存最后一次？
                    clickedPoints.clear()
                }
                linesParameters.forEach { line ->
                    if (line.lineType == LineType.DEFAULT_LINE) {
                        drawDefaultLineWithShadow(
                            line = line,
                            lowerValue = lowerValue.toFloat(),
                            upperValue = upperValue.toFloat(),
                            animatedProgress = animatedProgress,
                            spacingX = finalSpacingX.dp,
                            spacingY = spacingY,
                            clickedPoints = clickedPoints,
                            textMeasure = textMeasure,
                            xRegionWidth = xRegionWidth
                        )
                    } else {
                        drawQuarticLineWithShadow(
                            line = line,
                            lowerValue = lowerValue.toFloat(),
                            upperValue = upperValue.toFloat(),
                            animatedProgress = animatedProgress,
                            spacingX = finalSpacingX.dp,
                            spacingY = spacingY,
                            specialChart = specialChart,
                            clickedPoints = clickedPoints,
                            xRegionWidth = xRegionWidth,
                            textMeasurer = textMeasure
                        )
                    }
                }
            }
        }
    }

    // ------------------ 动画控制 ------------------
    LaunchedEffect(linesParameters, animateChart) {
        if (animateChart) {
            collectToSnapShotFlow(linesParameters) {
                upperValue = it.getUpperValue()
                lowerValue = it.getLowerValue()
            }
            delay(400)
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }
}

// ------------------ 工具函数区 ------------------
private fun List<LineParameters>.getUpperValue(): Double {
    return this.flatMap { item -> item.data }.maxOrNull()?.plus(1.0) ?: 0.0
}

private fun List<LineParameters>.getLowerValue(): Double {
    return this.flatMap { item -> item.data }.minOrNull() ?: 0.0
}

private fun CoroutineScope.collectToSnapShotFlow(
    linesParameters: List<LineParameters>,
    makeUpdateData: (List<LineParameters>) -> Unit,
) {
    this.launch {
        snapshotFlow {
            linesParameters
        }.collect {
            makeUpdateData(it)
        }
    }
}


fun Dp.toPx(): Float {
    return this.value
}




