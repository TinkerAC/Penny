package app.penny.feature.setting.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun <T> ExposedDropDownSetting(
    settingName: String,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    displayMapper: (T) -> String // Function to get the display text from T
) {
    var expanded by remember { mutableStateOf(false) } // Controls the dropdown menu state

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ).padding(12.dp).clickable(onClick = { expanded = !expanded })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Aligns title and chip at ends
        ) {
            // Left side: Setting name
            Text(
                text = settingName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            // Right side: Selected item as a Chip with dropdown functionality
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    FilterChip(
                        selected = true, // Always selected to display the current selection
                        onClick = { expanded = !expanded }, // Toggle dropdown on click
                        label = {
                            Text(
                                text = displayMapper(selectedItem),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = if (expanded) "Collapse" else "Expand"
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    )
                    // Dropdown Menu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        items.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = displayMapper(item),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (item == selectedItem) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (item == selectedItem) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onItemSelected(item) // Trigger selection callback
                                    expanded = false // Close dropdown
                                }
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.width(8.dp)) // Space between Chip and other potential elements
            }


        }
    }
}
