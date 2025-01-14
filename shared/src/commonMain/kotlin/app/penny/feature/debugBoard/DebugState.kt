// file: /Users/tinker/StudioProjects/Penny/shared/src/commonMain/kotlin/app/penny/feature/debugBoard/DebugState.kt
package app.penny.feature.debugBoard

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import kotlinx.datetime.Instant
import kotlinx.io.files.Path

data class DebugState(
    val transactions: List<TransactionModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val addTransactionModalVisible: Boolean = false,
    val lastSyncedAt: Instant? = null,
    val message: String? = null,
    val activeUser: UserModel? = null,
    val defaultLedger: LedgerModel? = null,
    val databasePath: Path? = null,
    val settingStorePath: Path? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val debugUrl : String? = null,
)
