package app.penny.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.domain.model.Transaction

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = transaction.id, modifier = Modifier.weight(1f))
        Text(text = transaction.amount.toString())
    }
}
