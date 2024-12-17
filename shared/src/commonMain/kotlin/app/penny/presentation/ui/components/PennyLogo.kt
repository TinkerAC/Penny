package app.penny.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.style.TextAlign
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PennyLogo(
    text: String = stringResource(SharedRes.strings.app_name),
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = MaterialTheme.typography.displaySmall.fontSize,
    fontFamily: FontFamily = fontFamilyResource(SharedRes.fonts.leckerlione_regular),
    fontWeight: androidx.compose.ui.text.font.FontWeight? = null,
    letterSpacing: TextUnit? = null,
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier,
    textStyle: TextStyle? = null
) {
    Text(
        text = text,
        color = color,
        modifier = modifier,
        style = textStyle ?: MaterialTheme.typography.displaySmall.copy(
            fontFamily = fontFamily,
            fontSize = fontSize,
            fontWeight = fontWeight ?: MaterialTheme.typography.displaySmall.fontWeight,
            letterSpacing = letterSpacing ?: MaterialTheme.typography.displaySmall.letterSpacing,
            textAlign = textAlign ?: MaterialTheme.typography.displaySmall.textAlign
        )
    )
}
