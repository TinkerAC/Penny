package app.penny.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource


@Composable
fun PennyLogo(
    color: Color = MaterialTheme.colorScheme.primary,
    text: String = stringResource(SharedRes.strings.app_name)
) {
    Text(
        text = text,
        color = color,
        style = typography.displaySmall.copy(
            fontFamily = fontFamilyResource(SharedRes.fonts.leckerlione_regular)
        )
    )
}