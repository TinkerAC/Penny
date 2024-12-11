// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep4InitLedger.kt
package app.penny.feature.onBoarding

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import app.penny.core.data.repository.UserDataRepository
import app.penny.di.getKoinInstance
import app.penny.feature.newLedger.*
import app.penny.presentation.ui.MainScreen
import app.penny.presentation.ui.components.CurrencySelectorModal
import app.penny.presentation.ui.components.PennyLogo
import app.penny.presentation.ui.theme.spacing
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class OnboardingStep4InitLedger : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun Content() {
        val viewModel: NewLedgerViewModel = koinScreenModel()
        val uiState by viewModel.uiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val rootNavigator = LocalNavigator.currentOrThrow
        // 处理 NewLedgerViewModel 事件流
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is NewLedgerUiEvent.ShowSnackBar -> {
                        coroutineScope.launch {
                            uiState.snackbarHostState.showSnackbar(
                                message = event.message,
                            )
                        }
                    }

                    is NewLedgerUiEvent.OnFinishInsert -> {
                        val userDataRepository = getKoinInstance<UserDataRepository>()

                        userDataRepository.setRecentLedgerUuid(event.newLedgerUuid)
                        rootNavigator.replaceAll(MainScreen())

                    }
                }
            }
        }

        OnboardingInitLedgerPage(
            title = "初始化您的账本",
            content = "请为您的账本设置名称和封面，完成后将开始使用Penny管理您的财务！",
            uiState = uiState,
            onIntent = { intent -> viewModel.handleIntent(intent) },
            viewModel = viewModel
        )
    }
}

@Composable
fun OnboardingInitLedgerPage(
    title: String,
    content: String,
    uiState: NewLedgerUiState,
    onIntent: (NewLedgerIntent) -> Unit,
    viewModel: NewLedgerViewModel
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = uiState.snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            PennyLogo()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = title,
                style = typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = content,
                style = typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // 中间区域：账本创建的UI组件 (内部有"创建账本"按钮)
            NewLedgerContent(
                uiState = uiState,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        }

        // 显示货币选择器 Modal，如果需要
        if (uiState.currencySelectorModalVisible) {
            CurrencySelectorModal(
                onDismiss = { onIntent(NewLedgerIntent.CloseCurrencySelectorModal) },
                viewModel = viewModel
            )
        }
    }
}