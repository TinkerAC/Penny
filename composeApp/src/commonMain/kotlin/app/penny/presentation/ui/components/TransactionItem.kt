package app.penny.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.domain.model.TransactionModel

@Composable
fun TransactionItem(transaction: TransactionModel) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Text(text = transaction.content ?: "No content")
        Text(text = transaction.amount.toString())
    }
}
