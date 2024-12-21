// YearlyTabContent.kt
package app.penny.feature.analytics.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.utils.localDateNow

@Composable
fun YearlyTabContent(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    val currentYear = localDateNow().year
    val years = (currentYear downTo currentYear - 10).toList()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "选择年份",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(years) { year ->
                YearCard(
                    year = year,
                    isSelected = selectedYear == year,
                    onClick = { onYearSelected(year) }
                )
            }
        }
    }
}

@Composable
fun YearCard(
    year: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .width(80.dp)
            .height(60.dp)
    ) {
        Box(
            contentAlignment = androidx.compose.ui.Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
