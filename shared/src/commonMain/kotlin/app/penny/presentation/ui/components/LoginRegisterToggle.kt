package app.penny.presentation.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun LoginRegisterToggle(
    isLoginMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    SliderToggleButton(
        options = listOf(
            stringResource(SharedRes.strings.login),
            stringResource(SharedRes.strings.register)
        ),

        selectedIndex = if (isLoginMode) 0 else 1,
        onToggle = {
            onToggle()
        },
        modifier = modifier.width(160.dp),
        selectedBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        sliderColor = MaterialTheme.colorScheme.primary,
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = CircleShape,
        animationDuration = 300
    )
}


