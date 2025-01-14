package app.penny.feature.toolBox.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToolCard(
    toolName: String,
    toolDescription: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth() // Make the card fill the available width
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp) // Internal padding
                .fillMaxWidth(), // Row takes full width
            verticalAlignment = Alignment.CenterVertically, // Center align items vertically
            horizontalArrangement = Arrangement.SpaceBetween // Space between icon-text and arrow
        ) {
            // Left Section: Icon and Name + Description
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Tool Icon
                Icon(
                    imageVector = icon,
                    contentDescription = toolName,
                    modifier = Modifier
                        .size(48.dp) // Size for the icon
                        .padding(end = 16.dp),
                    tint = MaterialTheme.colorScheme.inversePrimary,
                )

                VerticalDivider(
                    modifier = Modifier.height(48.dp)
                        .padding(top = 4.dp, bottom = 4.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                // Tool Name
                Text(
                    text = toolName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
//
            }

            // Right Section: Arrow Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                modifier = Modifier.size(24.dp), // Arrow size
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
