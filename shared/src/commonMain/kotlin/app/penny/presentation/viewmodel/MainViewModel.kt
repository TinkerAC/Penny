package app.penny.presentation.viewmodel

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.presentation.uiState.MainUiState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
class MainViewModel(
    private val ledgerRepository: LedgerRepository,
    private val userDataRepository: UserDataRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()


    init {
        screenModelScope.launch {
            fetchLedgers()
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    fun fetchLedgers() {
        // 通过协程获取数据
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(
                ledgers = ledgerRepository.findByUserUuid(
                    userUuid = userDataRepository.getUserUuid()
                )
            )
        }
    }


}


