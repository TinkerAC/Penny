package app.penny.feature.myLedger

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.usecase.GetAllLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class MyLedgerViewModel(
    private val getAllLedgerUseCase: GetAllLedgerUseCase,
    private val ledgerRepository: LedgerRepository
    ) : ScreenModel {


    private val _uiState = MutableStateFlow(MyLedgerUiState())
    val uiState: StateFlow<MyLedgerUiState> = _uiState.asStateFlow()

    init {
        getLedgers()
    }


    fun handleIntent(intent: MyLedgerIntent) {
        when (intent) {
            MyLedgerIntent.RefreshLedgers -> getLedgers()

        }
    }


    private fun getLedgers() {
        // 通过协程获取数据
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val ledgers = getAllLedgerUseCase()
            _uiState.value = _uiState.value.copy(ledgers = ledgers, isLoading = false)
        }
    }

    fun deleteLedger(ledgerModel: LedgerModel) {
        screenModelScope.launch {
            ledgerRepository.deleteByUuid(ledgerModel.uuid)
        }
    }


}