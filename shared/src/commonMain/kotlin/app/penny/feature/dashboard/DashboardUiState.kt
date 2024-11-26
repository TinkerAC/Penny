package app.penny.feature.dashboard

import app.penny.core.domain.model.TransactionModel
import kotlinx.datetime.Instant

data class DashboardUiState(
    val transactions: List<TransactionModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val addTransactionModalVisible: Boolean = false,
    val lastSyncedAt: Instant? = null,
    val message: String? = null
)
