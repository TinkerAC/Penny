// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/components/ListViewContent.kt
package app.penny.feature.transactions.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.feature.transactions.GroupedTransaction
import app.penny.feature.transactions.TransactionUiState
import app.penny.feature.transactions.TransactionViewModel
import co.touchlab.kermit.Logger


@Composable
fun ListViewContent(
    viewModel: TransactionViewModel,
    uiState: TransactionUiState,
) {
    Column {
        TransactionList(
            groupedTransactions = uiState.groupedTransactions,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun TransactionList(
    groupedTransactions: List<GroupedTransaction>,
    modifier: Modifier = Modifier
) {
    Logger.d("TransactionList: ${groupedTransactions.size}")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        // Add vertical spacing between items
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(groupedTransactions) { group ->
            TransactionGroup(group)
        }
    }
}