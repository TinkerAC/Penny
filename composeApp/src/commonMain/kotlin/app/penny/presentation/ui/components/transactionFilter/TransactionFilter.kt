package app.penny.presentation.ui.components.transactionFilter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.penny.presentation.ui.screens.transactions.TransactionViewModel

//@Composable
//fun TransactionFilterBar(
//    modifier: Modifier,
//    viewModel: TransactionViewModel,
//) {
//    val uiState = viewModel.uiState
//
//    BottomAppBar(
//        modifier = Modifier.fillMaxWidth(),
//        containerColor = MaterialTheme.colorScheme.primaryContainer
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            // 为每个过滤类别创建一个 FilterChip
//            .values().forEach { category ->
//                val selectedOption = selectedGroupBys[category]
//                FilterChip(
//                    selected = selectedOption != null,
//                    onClick = { onFilterClicked(category, selectedOption) },
//                    label = {
//                        Text(
//                            text = selectedOption?.displayText ?: "All ${category.name}"
//                        )
//                    },
//                    modifier = Modifier.padding(4.dp)
//                )
//            }
//        }
//    }
//}
//
//
//}
//
//
//
