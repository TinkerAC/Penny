package app.penny.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionModal(
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxSize(),

        ) {
        Button(onClick = {

        }) {
            Text(stringResource(SharedRes.strings.add_transaction))
        }
    }

}