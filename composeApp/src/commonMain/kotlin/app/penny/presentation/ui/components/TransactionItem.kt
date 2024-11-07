package app.penny.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.domain.model.TransactionModel

@Composable
fun TransactionItem(transaction: TransactionModel) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Text(text = transaction.remark ?: "No Remark")
        Text(text = transaction.amount.toString())
    }
}
