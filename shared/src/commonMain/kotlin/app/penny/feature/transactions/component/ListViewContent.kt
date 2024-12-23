// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/components/ListViewContent.kt
package app.penny.feature.transactions.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.feature.transactions.GroupedTransaction
import app.penny.feature.transactions.TransactionUiState


@Composable
fun ListViewContent(
    uiState: TransactionUiState,
) {
    Column(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        TransactionList(
            groupedTransactions = uiState.groupedTransactions,
            modifier = Modifier.fillMaxSize(),
        )
    }
}


@Composable
fun TransactionList(
    groupedTransactions: List<GroupedTransaction>,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        // Add vertical spacing between items
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        items(groupedTransactions) { group ->
            TransactionGroup(group)
        }

    }


}

