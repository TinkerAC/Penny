// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep4InitLedger.kt
package app.penny.feature.onBoarding

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import app.penny.core.data.repository.UserDataRepository
import app.penny.di.getKoinInstance
import app.penny.feature.newLedger.NewLedgerContent
import app.penny.feature.newLedger.NewLedgerIntent
import app.penny.feature.newLedger.NewLedgerUiEvent
import app.penny.feature.newLedger.NewLedgerUiState
import app.penny.feature.newLedger.NewLedgerViewModel
import app.penny.presentation.ui.MainScreen
import app.penny.presentation.ui.components.CurrencySelectorModal
import app.penny.presentation.ui.components.PennyLogo
import app.penny.presentation.ui.theme.spacing
import app.penny.shared.SharedRes
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
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
        val rootNavigator = LocalNavigator.currentOrThrow.parent
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

                        userDataRepository.setDefaultLedger(
                            ledger = event.newLedger
                        )
                        rootNavigator?.replaceAll(MainScreen())
                    }
                }
            }
        }

        OnboardingInitLedgerPage(
            title = stringResource(SharedRes.strings.set_up_your_ledger),
            content = stringResource(SharedRes.strings.set_up_your_ledger_description),
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
    val keyboardController = LocalSoftwareKeyboardController.current//for hiding keyboard on IOS

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = uiState.snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            keyboardController?.hide()
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            PennyLogo()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = title,
                style = typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
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