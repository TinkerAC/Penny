package app.penny.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PennyTopBar(
    title: String,
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = Modifier.fillMaxWidth()
    )


}