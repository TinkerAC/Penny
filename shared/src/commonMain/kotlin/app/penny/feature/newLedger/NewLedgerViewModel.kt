// shared/src/commonMain/kotlin/app/penny/feature/newLedger/viewmodel/NewLedgerViewModel.kt
package app.penny.feature.newLedger

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.enumerate.Currency
import app.penny.core.domain.enumerate.LedgerCover
import app.penny.core.domain.model.LedgerModel
import app.penny.platform.getRawStringResource
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class NewLedgerViewModel(
    private val ledgerRepository: LedgerRepository,
    private val userDataRepository: UserDataRepository
) : ScreenModel {

    // UI 状态
    private val _uiState = MutableStateFlow(NewLedgerUiState())
    val uiState: StateFlow<NewLedgerUiState> = _uiState.asStateFlow()

    // 一次性事件
    private val _eventFlow = MutableSharedFlow<NewLedgerUiEvent>(replay = 0)
    val eventFlow = _eventFlow.asSharedFlow()

    // 处理用户意图
    fun handleIntent(intent: NewLedgerIntent) {
        when (intent) {
            is NewLedgerIntent.OpenCurrencySelectorModal -> openCurrencySelectorModal()
            is NewLedgerIntent.CloseCurrencySelectorModal -> closeCurrencySelectorModal()
            is NewLedgerIntent.ConfirmCreateLedger -> confirmCreateLedger()
            is NewLedgerIntent.SelectCover -> selectCover(intent.cover)
            is NewLedgerIntent.SetLedgerName -> setLedgerName(intent.name)
            is NewLedgerIntent.SelectCurrency -> selectCurrency(intent.currency)
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
            if (!validateLedgerName()) {
                _eventFlow.emit(
                    NewLedgerUiEvent.ShowSnackBar(
                        getRawStringResource(SharedRes.strings.ledger_name_cannot_be_empty)
                    )
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val newLedger = LedgerModel(
                    uuid = Uuid.random(),
                    userUuid = userDataRepository.getUser().uuid,
                    name = _uiState.value.ledgerName,
                    currency = _uiState.value.currency,
                    cover = _uiState.value.ledgerCover,
                    description = ""
                )
                ledgerRepository.insert(newLedger)

                if (ledgerRepository.countByUser(userDataRepository.getUser()).toInt() == 1) {
                    userDataRepository.setDefaultLedger(newLedger)
                }


                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(
                    NewLedgerUiEvent.ShowSnackBar(
                        getRawStringResource(SharedRes.strings.ledger_created_successfully)
                    )
                )
                _eventFlow.emit(NewLedgerUiEvent.OnFinishInsert(newLedger))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(NewLedgerUiEvent.ShowSnackBar(e.message ?: "Unknown error"))
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
        _uiState.value =
            _uiState.value.copy(currency = currency, currencySelectorModalVisible = false)
    }

    // 验证账本名称
    private fun validateLedgerName(): Boolean = _uiState.value.ledgerName.isNotBlank()
}
