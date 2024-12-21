package app.penny.feature.debugBoard

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import kotlinx.datetime.Instant

data class DebugState(
    val transactions: List<TransactionModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val addTransactionModalVisible: Boolean = false,
    val lastSyncedAt: Instant? = null,
    val message: String? = null,
    val activeUser: UserModel? = null,
    val defaultLedger:LedgerModel? = null
)
