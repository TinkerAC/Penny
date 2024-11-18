package app.penny.presentation.ui.screens.myLedger

import app.penny.domain.model.LedgerModel
import app.penny.domain.usecase.DeleteLedgerUseCase
import app.penny.domain.usecase.GetAllLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MyLedgerViewModel(
    private val deleteLedgerUseCase: DeleteLedgerUseCase,
    private val getAllLedgerUseCase: GetAllLedgerUseCase,

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
            deleteLedgerUseCase(ledgerModel.id)
        }
    }


}