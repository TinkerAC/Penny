package app.penny.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import penny.shared.generated.resources.LeckerliOne_Regular
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.app_name


@Composable
fun PennyLogo(
    color: Color = MaterialTheme.colorScheme.primary,
    text: String = stringResource(MR.strings.app_name)
) {
    Text(
        text = text,
        color = color,
        style = typography.displaySmall.copy(
            fontFamily = FontFamily(Font(Res.font.LeckerliOne_Regular))
        )
    )
}