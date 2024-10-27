package app.penny.presentation.viewmodel


import app.penny.data.repository.TransactionRepository
import app.penny.domain.model.Transaction
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ScreenModel {


    // 使用 MutableStateFlow 保存数据列表
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()


    //通过协程获取数据
    fun fetchTransactions() {
        screenModelScope.launch {
            _transactions.value = transactionRepository.getTransactions()
        }
    }

    fun insertTransaction() {
        screenModelScope.launch {
            transactionRepository.insertTransaction()
        }
    }



}


