package app.penny.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A toggle button that allows users to switch between multiple options.
 *
 * @param options A list of options to choose from.
 * @param selectedIndex The index of the currently selected option.
 * @param onToggle Callback to switch options, passing the new selected index
 * @param modifier Modifier
 * @param selectedBackgroundColor The background color when the option is selected
 * @param unselectedBackgroundColor The background color when the option is not selected
 * @param sliderColor The color of the slider
 * @param textStyle The text style of the options
 * @param shape The shape of the button
 * @param animationDuration The duration of the animation when switching options
 */
@Composable
fun SliderToggleButton(
    options: List<String>,
    selectedIndex: Int,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    unselectedBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    sliderColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    shape: Shape = CircleShape,
    animationDuration: Int = 300
) {
    require(options.size >= 2) { "SliderToggleButton requires at least 2 options" }

    val toggleState = selectedIndex.toFloat()
    val positionFraction by animateFloatAsState(
        targetValue = toggleState,
        animationSpec = tween(durationMillis = animationDuration)
    )

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .background(
                color = selectedBackgroundColor,
                shape = shape
            )
            .clickable {
                // 切换到下一个选项
                val newIndex = (selectedIndex + 1) % options.size
                onToggle(newIndex)
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 动态计算滑块和容器的宽度
            var sliderWidth by remember { mutableStateOf(0f) }
            var totalWidth by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        totalWidth = coordinates.size.width.toFloat()
                        sliderWidth = totalWidth / options.size
                    }
            ) {
                // The slider
                Box(
                    modifier = Modifier
                        .width(with(LocalDensity.current) { sliderWidth.toDp() })
                        .fillMaxHeight()
                        .offset {
                            IntOffset(
                                x = (positionFraction * sliderWidth).roundToInt(),
                                y = 0
                            )
                        }
                        .background(
                            color = sliderColor,
                            shape = shape
                        )
                )

                // 标签
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    options.forEachIndexed { index, text ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = text,
                                color = if (index == selectedIndex) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                style = textStyle,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
