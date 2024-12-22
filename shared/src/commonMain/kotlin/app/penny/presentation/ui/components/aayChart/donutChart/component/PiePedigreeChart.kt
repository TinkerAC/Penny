package app.penny.presentation.ui.components.aayChart.donutChart.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.aayChart.donutChart.model.ChartTypes
import app.penny.presentation.ui.components.aayChart.donutChart.model.PieChartData
import app.penny.presentation.ui.components.aayChart.lineChart.toPx
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
internal fun DrawScope.drawPedigreeChart(
    pieValueWithRatio: List<Float>,
    pieChartData: List<PieChartData>,
    totalSum: Float,
    transitionProgress: Animatable<Float, AnimationVector1D>,
    textMeasure: TextMeasurer,
    textRatioStyle: TextStyle,
    ratioLineColor: Color,
    arcWidth: Float,
    minValue: Float,
    pieChart: ChartTypes,
    showSideLegend: Boolean = false,
    labelColor: Color
) {
    val outerCircularRadius = (minValue / 2) + (arcWidth / 1.2f)
    var startArc = -90F
    var startArcWithoutAnimation = -90f

    // 用来记录已经放置过的文字区域，避免重叠
    val usedLegendBounds = mutableListOf<Rect>()

    pieValueWithRatio.forEachIndexed { index, _ ->
        // ---- 计算动画角度 & 无动画角度 ----
        val arcWithAnimation = calculateAngle(
            dataLength = pieChartData[index].data.toFloat(),
            totalLength = totalSum,
            progress = transitionProgress.value
        )
        val arcWithoutAnimation = calculateAngle(
            dataLength = pieChartData[index].data.toFloat(),
            totalLength = totalSum
        )

        // ---- 取中心角的弧度值，用来确定引线、文字等位置 ----
        val angleInRadians = (startArcWithoutAnimation + arcWithoutAnimation / 2).degreeToAngle

        if (pieChart == ChartTypes.PIE_CHART) {
            // ---------- 饼图 ----------
            // 微调这里，让引线稍微插入到圆弧中，缩短引线
            val lineStart = Offset(
                x = center.x + (outerCircularRadius * 1.15f) * cos(angleInRadians) * 0.75f,
                y = center.y + (outerCircularRadius * 1.15f) * sin(angleInRadians) * 0.75f
            )
            val lineEnd = Offset(
                x = center.x + (outerCircularRadius * 1.15f) * cos(angleInRadians) * 1.05f,
                y = center.y + (outerCircularRadius * 1.15f) * sin(angleInRadians) * 1.05f
            )
            val arcOffset = Offset(
                center.x - (minValue / 2),
                center.y - (minValue / 2)
            )
            val region = pieValueWithRatio.subList(0, index).sum()
            val regionSign = if (region >= 180f) 1 else -1

            // 将最后一段水平线适当缩短
            val secondLineEnd = Offset(
                x = lineEnd.x + (arcWidth * 0.7f * regionSign),
                y = lineEnd.y
            )

            // 先画连线（可根据需求使用 ratioLineColor 或其他颜色）
            drawLines(ratioLineColor, lineStart, lineEnd, secondLineEnd)

            // 放大绘制，让扇形更突出
            scale(1.3f) {
                drawArc(
                    color = pieChartData[index].color,
                    startAngle = startArc,
                    sweepAngle = arcWithAnimation,
                    useCenter = true,
                    size = Size(minValue, minValue),
                    topLeft = arcOffset
                )
            }

            // 计算文字锚点（基准点）
            val textOffset = getTextOffsetByRegion(regionSign, lineEnd.x, secondLineEnd.y, arcWidth)
            // 让文字上移一点，避免和线条重叠
            val originalOffset = Offset(
                x = textOffset.x,
                y = textOffset.y -10.dp.toPx()
            )
            val ratioValue = getPartRatio(pieValueWithRatio, index)

            // 如果 showSideLegend = true，则在同一行绘制「百分比 + PartName」
            if (showSideLegend) {
                val partName = pieChartData[index].partName
                val isLeftSide = lineEnd.x < center.x
                // 在绘制前，先计算不会与已放置文字重叠的位置
                val (finalOffset, totalBounds) = measureRatioAndLabelOnOneLine(
                    textMeasurer = textMeasure,
                    ratioValue = ratioValue,
                    labelText = partName,
                    textStyle = textRatioStyle.copy(color = labelColor),
                    isLeftSide = isLeftSide,
                    anchorOffset = originalOffset,
                    usedLegendBounds = usedLegendBounds
                )
                // 将该文字区域记录下来
                usedLegendBounds.add(totalBounds)
                // 绘制
                drawRatioAndLabelOnOneLine(
                    textMeasurer = textMeasure,
                    ratioValue = ratioValue,
                    labelText = partName,
                    textStyle = textRatioStyle.copy(color = labelColor),
                    isLeftSide = isLeftSide,
                    anchorOffset = finalOffset
                )
            } else {
                // 否则只绘制百分比
                val textLayout = textMeasure.measure(
                    text = "$ratioValue%",
                    style = textRatioStyle.copy(color = labelColor)
                )
                // 先试着找不会重叠的偏移位置
                val finalOffset = findNonOverlapOffset(
                    originalOffset,
                    textLayout.size.width.toFloat(),
                    textLayout.size.height.toFloat(),
                    usedLegendBounds
                )
                // 计算该文字最终矩形
                val rect = Rect(
                    finalOffset.x,
                    finalOffset.y,
                    finalOffset.x + textLayout.size.width,
                    finalOffset.y + textLayout.size.height
                )
                usedLegendBounds.add(rect)

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        finalOffset.x - textLayout.size.width / 2,
                        finalOffset.y - textLayout.size.height / 2
                    )
                )
            }

            startArc += arcWithAnimation
            startArcWithoutAnimation += arcWithoutAnimation

        } else {
            // ---------- 环图 ----------
            // 同样微调
            val lineStart = Offset(
                x = center.x + (outerCircularRadius * 1.15f) * cos(angleInRadians) * 0.75f,
                y = center.y + (outerCircularRadius * 1.15f) * sin(angleInRadians) * 0.75f
            )
            val lineEnd = Offset(
                x = center.x + (outerCircularRadius * 1.15f) * cos(angleInRadians) * 1.05f,
                y = center.y + (outerCircularRadius * 1.15f) * sin(angleInRadians) * 1.05f
            )
            val arcOffset = Offset(
                center.x - (minValue / 2),
                center.y - (minValue / 2)
            )
            val region = pieValueWithRatio.subList(0, index).sum()
            val regionSign = if (region >= 180f) 1 else -1
            val secondLineEnd = Offset(
                x = lineEnd.x + (arcWidth * 0.7f * regionSign),
                y = lineEnd.y
            )
            drawLines(ratioLineColor, lineStart, lineEnd, secondLineEnd)

            // 绘制环形
            drawArc(
                color = pieChartData[index].color,
                startAngle = startArc,
                sweepAngle = arcWithAnimation,
                useCenter = false,
                style = Stroke(arcWidth, cap = StrokeCap.Butt),
                size = Size(minValue, minValue),
                topLeft = arcOffset
            )

            val textOffset = getTextOffsetByRegion(regionSign, lineEnd.x, secondLineEnd.y, arcWidth)
            val originalOffset = Offset(
                x = textOffset.x,
                y = textOffset.y - 40.toDp().toPx()
            )
            val ratioValue = getPartRatio(pieValueWithRatio, index)

            if (showSideLegend) {
                val partName = pieChartData[index].partName
                val isLeftSide = lineEnd.x < center.x

                val (finalOffset, totalBounds) = measureRatioAndLabelOnOneLine(
                    textMeasurer = textMeasure,
                    ratioValue = ratioValue,
                    labelText = partName,
                    textStyle = textRatioStyle.copy(color = Color.Black),
                    isLeftSide = isLeftSide,
                    anchorOffset = originalOffset,
                    usedLegendBounds = usedLegendBounds
                )
                usedLegendBounds.add(totalBounds)

                drawRatioAndLabelOnOneLine(
                    textMeasurer = textMeasure,
                    ratioValue = ratioValue,
                    labelText = partName,
                    textStyle = textRatioStyle.copy(color = labelColor),
                    isLeftSide = isLeftSide,
                    anchorOffset = finalOffset
                )
            } else {
                val textLayout = textMeasure.measure(
                    text = "$ratioValue%",
                    style = textRatioStyle.copy(color = labelColor)
                )
                val finalOffset = findNonOverlapOffset(
                    originalOffset,
                    textLayout.size.width.toFloat(),
                    textLayout.size.height.toFloat(),
                    usedLegendBounds
                )
                val rect = Rect(
                    finalOffset.x,
                    finalOffset.y,
                    finalOffset.x + textLayout.size.width,
                    finalOffset.y + textLayout.size.height
                )
                usedLegendBounds.add(rect)

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        finalOffset.x - textLayout.size.width / 2,
                        finalOffset.y - textLayout.size.height / 2
                    )
                )
            }

            startArc += arcWithAnimation
            startArcWithoutAnimation += arcWithoutAnimation
        }
    }
}

/**
 * 在同一行绘制「百分比 + PartName」。
 * 但在真正绘制前，先用此方法计算文本的最终摆放位置，并返回该位置以及对应的文字区域 Rect。
 */
@OptIn(ExperimentalTextApi::class)
private fun DrawScope.measureRatioAndLabelOnOneLine(
    textMeasurer: TextMeasurer,
    ratioValue: Int,
    labelText: String,
    textStyle: TextStyle,
    isLeftSide: Boolean,
    anchorOffset: Offset,
    usedLegendBounds: MutableList<Rect>
): Pair<Offset, Rect> {
    // 先测量「百分比」
    val ratioTextLayout = textMeasurer.measure(
        text = "$ratioValue%",
        style = textStyle
    )
    // 再测量「PartName」
    val labelLayout = textMeasurer.measure(
        text = labelText,
        style = textStyle
    )
    val spacingPx = 8.dp.toPx()
    val totalWidth = ratioTextLayout.size.width + spacingPx + labelLayout.size.width
    val maxHeight = maxOf(ratioTextLayout.size.height, labelLayout.size.height).toFloat()

    val baseX = if (isLeftSide) {
        anchorOffset.x - totalWidth // 往左减去总宽度
    } else {
        anchorOffset.x
    }
    val baseY = anchorOffset.y - (maxHeight / 2)

    // 先尝试放在 baseX, baseY
    val finalOffset = findNonOverlapOffset(
        originalOffset = Offset(baseX, baseY),
        totalWidth = totalWidth,
        totalHeight = maxHeight,
        usedLegendBounds = usedLegendBounds
    )
    // 返回该文字区域对应的 Rect
    val rect = Rect(
        left = finalOffset.x,
        top = finalOffset.y,
        right = finalOffset.x + totalWidth,
        bottom = finalOffset.y + maxHeight
    )
    return finalOffset to rect
}

/**
 * 真正绘制「百分比 + PartName」的逻辑。
 * 注意：此方法不再做重叠检测，调用前请先用 measureRatioAndLabelOnOneLine 计算可用位置。
 */
@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawRatioAndLabelOnOneLine(
    textMeasurer: TextMeasurer,
    ratioValue: Int,
    labelText: String,
    textStyle: TextStyle,
    isLeftSide: Boolean,
    anchorOffset: Offset
) {
    val ratioTextLayout = textMeasurer.measure(
        text = "$ratioValue%",
        style = textStyle
    )
    val labelLayout = textMeasurer.measure(
        text = labelText,
        style = textStyle
    )

    val spacingPx = 8.dp.toPx()
    val totalWidth = ratioTextLayout.size.width + spacingPx + labelLayout.size.width
    val maxHeight = maxOf(ratioTextLayout.size.height, labelLayout.size.height)

    // 最终 X 坐标（传进来的 anchorOffset 已经处理过重叠，可直接使用）
    val baseX = anchorOffset.x
    val baseY = anchorOffset.y


    // 先画「百分比」
    drawText(
        textLayoutResult = if (!isLeftSide) {
            ratioTextLayout
        } else {
            labelLayout
        },
        topLeft = Offset(
            baseX,
            baseY
        )
    )
    // 再画「PartName」
    val labelX =
        baseX + (if (!isLeftSide) ratioTextLayout.size.width else labelLayout.size.width) + spacingPx
    drawText(
        textLayoutResult = if (!isLeftSide) {
            labelLayout
        } else {
            ratioTextLayout
        },
        topLeft = Offset(
            labelX,
            baseY
        )
    )


}

/**
 * 针对只需要画「百分比」时的重叠检测，可以直接在外层处理。
 * 这里保留原本的方法，如需自定义可省略。
 */
@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawRatioOnly(
    textMeasurer: TextMeasurer,
    ratioValue: Int,
    textStyle: TextStyle,
    anchorOffset: Offset
) {
    val textLayout = textMeasurer.measure(
        text = "$ratioValue%",
        style = textStyle
    )
    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            anchorOffset.x - textLayout.size.width / 2,
            anchorOffset.y - textLayout.size.height / 2
        )
    )
}

// ------------------ 其他辅助函数 ------------------
private val Float.degreeToAngle
    get() = (this * (22 / 7f) / 180f)

/**
 * 计算动画时的角度
 */
private fun calculateAngle(dataLength: Float, totalLength: Float, progress: Float = 1f): Float {
    return -360F * dataLength * progress / totalLength
}

/**
 * 获取该扇形所占的“百分比”（0~100）
 */
private fun getPartRatio(pieValueWithRatio: List<Float>, index: Int): Int {
    return (pieValueWithRatio[index].toDouble() / 360.0 * 100).roundToInt()
}

/**
 * 计算连线末端附近的“文字基准位置”
 */
private fun getTextOffsetByRegion(
    regionSign: Int,
    x: Float,
    y: Float,
    arcWidth: Float
): Offset {
    return if (regionSign == 1) {
        Offset(x + arcWidth / 4, y)
    } else {
        Offset(x - arcWidth, y)
    }
}


/**
 * 在放置文字时，判断是否与已放置文字区重叠。
 * 如果重叠则向上/向下微调位置，直至无重叠或达到尝试上限。
 */
private fun findNonOverlapOffset(
    originalOffset: Offset,
    totalWidth: Float,
    totalHeight: Float,
    usedLegendBounds: MutableList<Rect>
): Offset {
    var candidateOffset = originalOffset
    val step = 10.dp.toPx()
    var tries = 0
    // 最多尝试 100 次，避免陷入死循环
    while (tries < 100) {
        val candidateRect = Rect(
            left = candidateOffset.x,
            top = candidateOffset.y,
            right = candidateOffset.x + totalWidth,
            bottom = candidateOffset.y + totalHeight
        )
        val isOverlap = usedLegendBounds.any { it.overlaps(candidateRect) }
        if (!isOverlap) {
            return candidateOffset
        }
        // 如果重叠，则往上移动一点试试
        candidateOffset = candidateOffset.copy(y = candidateOffset.y - step)
        tries++
    }
    // 如果尝试多次仍未找到合适位置，直接返回原始位置（或可按需处理）
    return originalOffset
}