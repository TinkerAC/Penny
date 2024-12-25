// file: src/commonMain/kotlin/app/penny/feature/transactionDetail/TransactionDetailScreen.kt
package app.penny.feature.transactionDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.penny.getRawStringResource
import app.penny.presentation.ui.components.FilterChipDropDown
import app.penny.presentation.ui.components.FilterChipDropDownStyle
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
private fun LocalFilterChipDropDownStyle(): FilterChipDropDownStyle {
    return FilterChipDropDownStyle(
        alwaysSelected = true,
        shape = MaterialTheme.shapes.small,
        border = null,
        filterChipColors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
        ),
        labelTextStyle = MaterialTheme.typography.labelMedium.copy()
    )
}

/**
 * 一个简单的包装后的 RemarkTextField，用来展示编辑备注。
 */
@Composable
fun RemarkTextField(
    remark: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = remark,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        maxLines = 3,
        label = {
            Text(
                text = stringResource(SharedRes.strings.remarks),
            )
        },
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

class TransactionDetailScreen @OptIn(ExperimentalUuidApi::class) constructor(
    private val transactionUuid: Uuid,
) : Screen {
    @OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel: TransactionDetailViewModel =
            koinScreenModel<TransactionDetailViewModel>(parameters = {
                parametersOf(transactionUuid)
            })
        val uiState = viewModel.uiState.collectAsState()

        // SnackBarHostState 用于展示Snackbar
        val snackBarHostState = SnackbarHostState()
        val coroutineScope = rememberCoroutineScope()

        // 金额输入框的 FocusRequester
        val amountFocusRequester = remember { FocusRequester() }

        // 收集只读提示事件等
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is TransactionDetailUiEvent.ShowSnackBar -> {
                        // 弹出SnackBar
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = getRawStringResource(event.message)
                            )
                        }
                    }

                    is TransactionDetailUiEvent.FocusAmountInput -> {
                        // 聚焦金额输入框
                        amountFocusRequester.requestFocus()
                    }
                }
            }
        }

        val localNavigator = LocalNavigator.currentOrThrow

        Scaffold(
            // 在 Scaffold 中放置一个 SnackBarHost
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            },
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(SharedRes.strings.transaction_details)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            localNavigator.pop()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                // 用户点击保存时，先校验金额
                                viewModel.handleIntent(TransactionDetailIntent.ValidateAmount)
                                // 再执行保存
                                viewModel.handleIntent(TransactionDetailIntent.SaveTransaction)
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Confirm"
                                )
                            },
                        )
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            },
            bottomBar = {
                // 底部操作区域
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // “删除”按钮
                    OutlinedButton(
                        onClick = {
                            viewModel.handleIntent(TransactionDetailIntent.DeleteTransaction)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(SharedRes.strings.delete))
                    }

                    // “完成”按钮
                    Button(
                        onClick = {
                            // 先校验
                            viewModel.handleIntent(TransactionDetailIntent.ValidateAmount)
                            // 再保存
                            viewModel.handleIntent(TransactionDetailIntent.SaveTransaction)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(SharedRes.strings.save))
                    }
                }
            }
        ) { innerPadding ->
            // 主体内容区域
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(1) {
                    TransactionEditSheet(
                        viewModel = viewModel,
                        uiState = uiState.value,
                        amountFocusRequester = amountFocusRequester
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionEditSheet(
    viewModel: TransactionDetailViewModel,
    uiState: TransactionDetailUiState,
    amountFocusRequester: FocusRequester
) {
    Column {
        // 1. 金额字段：只显示 amountText，不改变现有UI样式
        AmountEditField(
            amountString = uiState.amountText,
            onAmountChange = { newAmount ->
                // 实时更新文本，不做解析
                viewModel.handleIntent(TransactionDetailIntent.UpdateAmountText(newAmount))
            },
            onAmountValidate = {
                // 当失去焦点时 -> 校验
                viewModel.handleIntent(TransactionDetailIntent.ValidateAmount)
            },
            errorMessage = uiState.amountInputError?.let { stringResource(it) },
            focusRequester = amountFocusRequester
        )

        // 分割线
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 2. 交易类型
        EditRow(
            icon = {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "transaction_type",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            title = stringResource(SharedRes.strings.transaction_type),
            content = {
                FilterChipDropDown(
                    items = uiState.alternativeTransactionType,
                    selectedItem = uiState.transactionModel.transactionType,
                    onItemSelected = { transactionType ->
                        viewModel.handleIntent(
                            TransactionDetailIntent.SelectTransactionType(transactionType)
                        )
                    },
                    displayMapper = { stringResource(it.displayName) },
                    styleConfig = LocalFilterChipDropDownStyle()
                )
            }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 3. 主/次分类
        EditRow(
            icon = {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = "primary_category",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            title = stringResource(SharedRes.strings.category),
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChipDropDown(
                        items = uiState.alternativeTransactionPrimaryCategory,
                        selectedItem = uiState.transactionModel.category.parentCategory!!,
                        onItemSelected = { primaryCategory ->
                            viewModel.handleIntent(
                                TransactionDetailIntent.SelectPrimaryCategory(primaryCategory)
                            )
                        },
                        displayMapper = { stringResource(it.categoryName) },
                        styleConfig = LocalFilterChipDropDownStyle()
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "arrow_forward",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    )

                    FilterChipDropDown(
                        items = uiState.alternativeTransactionSecondaryCategory,
                        selectedItem = uiState.transactionModel.category,
                        onItemSelected = { secondaryCategory ->
                            viewModel.handleIntent(
                                TransactionDetailIntent.SelectSecondaryCategory(secondaryCategory)
                            )
                        },
                        displayMapper = { stringResource(it.categoryName) },
                        styleConfig = LocalFilterChipDropDownStyle()
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 4. 账户（只读）
        EditRow(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "ledger",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            title = stringResource(SharedRes.strings.ledger),
            content = {
                Text(
                    text = uiState.belongingLedger.name + "(${uiState.belongingLedger.currency.currencyCode})",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            },
            modifier = Modifier.clickable {
                // 只读提示
                viewModel.handleIntent(TransactionDetailIntent.ClickLedgerField)
            }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 5. 时间（只读）
        EditRow(
            icon = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "time",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            title = stringResource(SharedRes.strings.time),
            content = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = uiState.transactionModel.transactionInstant.toLocalDateTime(
                            timeZone = kotlinx.datetime.TimeZone.currentSystemDefault()
                        ).toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            modifier = Modifier.clickable {
                // 只读提示
                viewModel.handleIntent(TransactionDetailIntent.ClickLedgerField)
            }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 6. 备注
        EditRow(
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "remark",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            title = stringResource(SharedRes.strings.remarks),
            content = {
                RemarkTextField(
                    remark = uiState.transactionModel.remark ?: "",
                    onValueChange = { newValue ->
                        viewModel.handleIntent(TransactionDetailIntent.UpdateRemark(newValue))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

/**
 * 金额编辑框：当焦点失去时，调用 onAmountValidate() 校验金额。
 * 这里注入 FocusRequester 来实现主动聚焦。
 */
@Composable
fun AmountEditField(
    amountString: String,
    onAmountChange: (String) -> Unit,
    onAmountValidate: () -> Unit,
    errorMessage: String?,
    focusRequester: FocusRequester
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = amountString,
            onValueChange = { newText -> onAmountChange(newText) },
            label = {
                Text(
                    stringResource(SharedRes.strings.amount)
                )
            },
            textStyle = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                // 应用 FocusRequester
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        // 失去焦点时进行校验
                        onAmountValidate()
                    }
                },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // 如果存在金额错误，则显示在下方
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * 简单的行布局
 */
@Composable
fun EditRow(
    icon: @Composable () -> Unit,
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Row(modifier = Modifier.weight(3f)) {
            icon()
            Spacer(Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp).weight(0.1f))

        Row(
            modifier = Modifier.weight(7f),
            horizontalArrangement = Arrangement.End
        ) {
            content()
        }
    }
}