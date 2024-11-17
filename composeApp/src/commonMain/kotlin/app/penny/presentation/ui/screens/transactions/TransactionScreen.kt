package app.penny.presentation.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.penny.domain.model.TransactionModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger

class TransactionScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel = koinScreenModel<TransactionViewModel>()

        val uiState by viewModel.uiState.collectAsState()
        val rootNavigator = LocalNavigator.currentOrThrow

        Box {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Transactions")
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                rootNavigator.pop()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                viewModel.handleIntent(TransactionIntent.ToggleView)
                            }) {
                                Icon(
                                    imageVector = if (uiState.isCalendarView)
                                        Icons.Default.List
                                    else
                                        Icons.Default.CalendarToday,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    if (!uiState.isCalendarView) {
                        GroupByBar(
                            uiState = uiState,
                            viewModel = viewModel,
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    when {
                        uiState.isCalendarView -> {
                            CalendarViewScreen().Content()
                        }

                        else -> {
                            TransactionListScreen(
                                viewModel = viewModel,
                                uiState = uiState
                            ).Content()
                        }

                    }
                }

            }

            if (uiState.sharedPopUpVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SharedPopup(
                        uiState = uiState,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}

class TransactionListScreen(
    val viewModel: TransactionViewModel,
    val uiState: TransactionUiState
) : Screen {
    @Composable
    override fun Content() {

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
        modifier = modifier.fillMaxSize()
    ) {
        items(groupedTransactions) { group ->
            GroupedTransactionItem(group)
        }
    }
}

@Composable
fun GroupedTransactionItem(group: GroupedTransaction) {
    var isExpanded by remember { mutableStateOf(true) }

    Column {
        // 组标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.groupKey,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
        // 交易列表
        if (isExpanded) {
            group.transactions.forEach { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionModel) {
    // 根据需要自定义交易项的显示
    Text(
        text = "${transaction.category.categoryName} - ${transaction.amount.toPlainString()}",
        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun GroupByBar(
    uiState: TransactionUiState,
    viewModel: TransactionViewModel,
) {
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            val groupByTypes = GroupByType.items
            groupByTypes.forEach { item ->
                val groupByType = item
                val isSelected = uiState.selectedGroupByType == groupByType
                val displayText = if (isSelected)
                    uiState.selectedGroupByOption?.displayText ?: groupByType.displayName
                else
                    groupByType.displayName

                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                ) {
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.handleIntent(
                                TransactionIntent.ShowSharedDropdown(
                                    groupByType
                                )
                            )
                        },
                        label = {
                            Text(
                                text = displayText
                            )
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun SharedPopup(
    uiState: TransactionUiState,
    viewModel: TransactionViewModel,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(uiState.groupByOptions.size) { index ->
                    val groupByOption = uiState.groupByOptions[index]
                    FilterChip(
                        selected = uiState.selectedGroupByOption == groupByOption,
                        onClick = {
                            viewModel.handleIntent(
                                TransactionIntent.SelectGroupByOption(
                                    groupByOption
                                )
                            )
                        },
                        label = {
                            Text(
                                text = groupByOption.displayText
                            )
                        },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}