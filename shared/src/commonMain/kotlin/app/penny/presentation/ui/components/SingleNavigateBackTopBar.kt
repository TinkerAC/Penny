package app.penny.presentation.ui.components

import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleNavigateBackTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    navigateBackEnabled: Boolean = true
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        title = {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                if (navigateBackEnabled) {
                    onNavigateBack()
                }
            }
            ) {

                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (navigateBackEnabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                    }
                )
            }
        }
//        },
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        )
    )
}