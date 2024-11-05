// NewLedgerViewModel.kt
package app.penny.presentation.ui.screens.newLedger

import app.penny.domain.enum.Currency
import app.penny.domain.enum.LedgerCover
import app.penny.domain.usecase.InsertLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewLedgerViewModel(
    private val insertLedgerUseCase: InsertLedgerUseCase
) : ScreenModel {

    // 管理 UI 状态的 StateFlow
    private val _uiState = MutableStateFlow(NewLedgerUiState())
    val uiState: StateFlow<NewLedgerUiState> = _uiState.asStateFlow()

    // 管理一次性事件的 SharedFlow
    private val _eventFlow = MutableSharedFlow<NewLedgerUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // 处理用户意图
    fun handleIntent(intent: NewLedgerIntent) {
        when (intent) {
            NewLedgerIntent.OpenCurrencySelectorModal -> openCurrencySelectorModal()
            NewLedgerIntent.CloseCurrencySelectorModal -> closeCurrencySelectorModal()
            NewLedgerIntent.ConfirmCreateLedger -> confirmCreateLedger()
            is NewLedgerIntent.SelectCover -> selectCover(ledgerCover = intent.cover)
            is NewLedgerIntent.SetLedgerName -> setLedgerName(name = intent.name)
            is NewLedgerIntent.SelectCurrency -> selectCurrency(currency = intent.currency)
        }
    }


    // 打开货币选择模态框
    private fun openCurrencySelectorModal() {
        _uiState.value = _uiState.value.copy(currencySelectorModalVisible = true)
    }

    // 关闭货币选择模态框
    private fun closeCurrencySelectorModal() {
        _uiState.value = _uiState.value.copy(currencySelectorModalVisible = false)
    }

    // 确认创建账本
    private fun confirmCreateLedger() {
        screenModelScope.launch {
            // 验证账本名称
            if (!validateLedgerName()) {
                _eventFlow.emit(NewLedgerUiEvent.ShowSnackbar("账本名称不能为空"))
                return@launch
            }

            // 设置加载状态
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 调用用例插入账本
                insertLedgerUseCase(
                    name = _uiState.value.ledgerName,
                    currency = _uiState.value.currency,
                    description = _uiState.value.ledgerDescription,
                    cover = _uiState.value.ledgerCover
                )
                // 设置加载完成状态
                _uiState.value = _uiState.value.copy(isLoading = false)
                // 发射成功事件
                _eventFlow.emit(NewLedgerUiEvent.ShowSnackbar("账本创建成功!"))
                _eventFlow.emit(NewLedgerUiEvent.NavigateBack)
            } catch (e: Exception) {
                // 设置加载完成状态
                _uiState.value = _uiState.value.copy(isLoading = false)
                // 发射错误事件
                _eventFlow.emit(NewLedgerUiEvent.ShowSnackbar(e.message ?: "创建账本失败"))
            }
        }
    }

    // 选择账本封面
    private fun selectCover(ledgerCover: LedgerCover) {
        _uiState.value = _uiState.value.copy(ledgerCover = ledgerCover)
    }

    // 设置账本名称
    private fun setLedgerName(name: String) {
        _uiState.value = _uiState.value.copy(ledgerName = name)
    }

    // 选择货币
    private fun selectCurrency(currency: Currency) {
        _uiState.value = _uiState.value.copy(currency = currency)
    }

    // 验证账本名称是否有效
    private fun validateLedgerName(): Boolean {
        return _uiState.value.ledgerName.isNotBlank()
    }
}
