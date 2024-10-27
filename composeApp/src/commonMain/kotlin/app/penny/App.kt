package app.penny

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import app.penny.presentation.ui.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}