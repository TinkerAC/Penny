package app.penny.feature.analytics.component.topbar

import app.penny.presentation.ui.components.DatePickerDialogComponent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate

@Composable
fun CustomTabContent(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit
) {
    var isStartDatePickerVisible by remember { mutableStateOf(false) }
    var isEndDatePickerVisible by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Range Title
        Text(
            text = stringResource(SharedRes.strings.select_date_range),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Start Date Section
        DatePickerSection(
            label = stringResource(SharedRes.strings.start_date),
            date = startDate,
            onClick = { isStartDatePickerVisible = true }
        )

        // End Date Section
        DatePickerSection(
            label = stringResource(SharedRes.strings.end_date),
            date = endDate,
            onClick = { isEndDatePickerVisible = true }
        )
    }


    // Date Picker Dialogs
    if (isStartDatePickerVisible) {
        DatePickerDialogComponent(
            initialSelectedDate = startDate,
            onDateSelected = { selectedDate ->
                if (selectedDate <= endDate) {
                    onStartDateSelected(selectedDate)
                }
            },
            onDismissRequest = { isStartDatePickerVisible = false },
            confirmText = stringResource(SharedRes.strings.confirm),
            cancelText = stringResource(SharedRes.strings.cancel)
        )
    }

    if (isEndDatePickerVisible) {
        DatePickerDialogComponent(
            initialSelectedDate = endDate,
            onDateSelected = { selectedDate ->
                if (selectedDate >= startDate) {
                    onEndDateSelected(selectedDate)
                }
            },
            onDismissRequest = { isEndDatePickerVisible = false },
            confirmText = stringResource(SharedRes.strings.confirm),
            cancelText = stringResource(SharedRes.strings.cancel)
        )
    }
}

@Composable
private fun DatePickerSection(
    label: String,
    date: LocalDate,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        DatePickerButton(
            selectedDate = date,
            onClick = onClick
        )
    }
}

@Composable
private fun DatePickerButton(
    selectedDate: LocalDate,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate.format(),
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun LocalDate.format(): String {
    return "$year-${month.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
}