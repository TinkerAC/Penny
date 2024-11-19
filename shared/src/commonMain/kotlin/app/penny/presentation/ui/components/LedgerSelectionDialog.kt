package app.penny.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.penny.domain.model.LedgerModel

@Composable
fun LedgerSelectionDialog(
    ledgers: List<LedgerModel>,
    onLedgerSelected: (LedgerModel) -> Unit,
    selectedLedger: LedgerModel?,
    onDismiss: () -> Unit

) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),

            ) {
            Column {
                Text(text = "Select a ledger")
                LazyColumn {
                    items(ledgers.size) { index ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(16.dp)
                                .clickable(
                                    onClick = {
                                        onLedgerSelected(ledgers[index])
                                        onDismiss()
                                    }
                                )
                        )
                        {
                            //radio button
                            RadioButton(
                                selected = selectedLedger == ledgers[index],
                                onClick = {
                                    onLedgerSelected(ledgers[index])
                                    onDismiss()
                                }
                            )
                            //ledger name
                            Text(
                                text = ledgers[index].name,
                                modifier = Modifier.padding(start = 8.dp)
                            )

                        }
                    }
                }
            }
        }
    }


}

