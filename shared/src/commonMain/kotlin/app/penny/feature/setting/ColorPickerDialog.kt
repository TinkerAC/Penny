package app.penny.feature.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPickerDialog(
    onDismissRequest: () -> Unit,
    onConfirmed: (Color) -> Unit,
    onCancel: () -> Unit
) {
    val controller = rememberColorPickerController()
    var color: Color by remember { mutableStateOf(Color.White) }
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp,
            modifier = Modifier.padding(16.dp) // Add padding for better spacing
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Color Picker
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        color = colorEnvelope.color
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons (Cancel and Confirm)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = {
                            onCancel()
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Confirm Button
                    Button(
                        onClick = {
                            onConfirmed(color)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}