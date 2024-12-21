// CustomTabContent.kt
package app.penny.feature.analytics.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

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
            .padding(16.dp)
    ) {
        // Start Date Selection
        Text(
            text = stringResource(SharedRes.strings.start_date),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        DatePickerButton(
            selectedDate = startDate,
            onClick = { isStartDatePickerVisible = true }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // End Date Selection
        Text(
            text = stringResource(SharedRes.strings.end_date),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        DatePickerButton(
            selectedDate = endDate,
            onClick = { isEndDatePickerVisible = true }
        )
    }

    // Start Date Picker Dialog
    if (isStartDatePickerVisible) {
        DatePickerDialogComponent(
            initialSelectedDate = startDate,
            onDateSelected = { selectedDate ->
                onStartDateSelected(selectedDate)
            },
            onDismissRequest = { isStartDatePickerVisible = false },
            confirmText = stringResource(SharedRes.strings.confirm),
            cancelText = stringResource(SharedRes.strings.cancel)
        )
    }

    // End Date Picker Dialog
    if (isEndDatePickerVisible) {
        DatePickerDialogComponent(
            initialSelectedDate = endDate,
            onDateSelected = { selectedDate ->
                onEndDateSelected(selectedDate)
            },
            onDismissRequest = { isEndDatePickerVisible = false },
            confirmText = stringResource(SharedRes.strings.confirm),
            cancelText = stringResource(SharedRes.strings.cancel)
        )
    }
}

@Composable
fun DatePickerButton(
    selectedDate: LocalDate,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = selectedDate.toString(), // Consider formatting the date as needed
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogComponent(
    initialSelectedDate: kotlinx.datetime.LocalDate?,
    onDateSelected: (kotlinx.datetime.LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    confirmText: String,
    cancelText: String
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate?.let {
            it.toEpochMilliseconds()
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val instant = Instant.fromEpochMilliseconds(selectedDateMillis)
                        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        onDateSelected(localDate)
                    }
                    onDismissRequest()
                }
            ) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = cancelText)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// Extension function to convert LocalDate to epoch milliseconds at start of day
fun kotlinx.datetime.LocalDate.toEpochMilliseconds(): Long {
    return this.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds * 1000
}

