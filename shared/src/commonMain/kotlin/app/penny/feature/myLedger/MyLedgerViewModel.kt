package app.penny.feature.myLedger

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.LedgerModel
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MyLedgerViewModel(
    private val ledgerRepository: LedgerRepository,
    private val userDataRepository: UserDataRepository,
    private val transactionRepository: TransactionRepository
) : ScreenModel {


    private val _uiState = MutableStateFlow(MyLedgerUiState())
    val uiState: StateFlow<MyLedgerUiState> = _uiState.asStateFlow()


    fun handleIntent(intent: MyLedgerIntent) {
        when (intent) {
            is MyLedgerIntent.SetDefaultLedger -> setDefaultLedger(intent.ledger)
        }
    }

    private fun setDefaultLedger(ledger: LedgerModel) {
        screenModelScope.launch {
            userDataRepository.setDefaultLedger(ledger)
            refreshData()
        }
    }


    fun refreshData() {
        // 通过协程获取数据
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val ledgers = ledgerRepository.findByUserUuid(
                userUuid = userDataRepository.getUser().uuid
            )

            val defaultLedger = userDataRepository.getDefaultLedger()
            _uiState.value = _uiState.value.copy(
                defaultLedger = defaultLedger,
                ledgers = ledgers,
                isLoading = false
            )
        }
    }

    fun getLedgerCount(ledgerUuid: Uuid): Long {
        var result: Long = 0
        screenModelScope.launch {
            result = transactionRepository.countByLedgerUuid(ledgerUuid)
        }
        return result
    }


}