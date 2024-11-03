package app.penny.presentation.ui.screens.newTransaction


import app.penny.data.model.TransactionType
import app.penny.data.repository.TransactionRepository
import app.penny.domain.model.TransactionModel
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class NewTransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ScreenModel {


    // 使用 MutableStateFlow 保存数据列表
    private val _transactions = MutableStateFlow<List<TransactionModel>>(emptyList())
    val transactions: StateFlow<List<TransactionModel>> = _transactions.asStateFlow()


    //通过协程获取数据
    fun fetchTransactions() {
        screenModelScope.launch {
            _transactions.value = transactionRepository.getAllTransactions()
        }
    }

    fun insertTransaction() {
        screenModelScope.launch {
            transactionRepository.insertTransaction(
                TransactionModel(
                    ledgerId = 1,
                    transactionDate = Clock.System.now().toEpochMilliseconds(),
                    categoryId = 1,
                    transactionType = TransactionType.EXPENSE,
                    amount = BigDecimal.ONE,
                    currencyCode = "CNY",
                    content = "sample",
                    screenshotUri = "",
                    note = "note"
                )
            )
        }
    }


}


