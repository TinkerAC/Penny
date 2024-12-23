package app.penny.feature.transactionDetail

import app.penny.core.data.repository.TransactionRepository
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TransactionDetailViewModel @OptIn(ExperimentalUuidApi::class) constructor(
    private val transactionUuid: Uuid,
    private val transactionRepository: TransactionRepository
) : ScreenModel {


    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState = _uiState.asStateFlow()



    init {
        screenModelScope.launch {
            _uiState.update {
                it.copy(
                    transactionModel = transactionRepository.findByUuid(transactionUuid)!!
                )
            }
        }

    }

}