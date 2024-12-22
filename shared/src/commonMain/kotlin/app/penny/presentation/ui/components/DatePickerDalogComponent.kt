package app.penny.presentation.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    initialSelectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    confirmText: String,
    cancelText: String
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate?.toEpochMilliseconds()
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val instant = Instant.fromEpochMilliseconds(selectedDateMillis)
                        val localDate =
                            instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
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