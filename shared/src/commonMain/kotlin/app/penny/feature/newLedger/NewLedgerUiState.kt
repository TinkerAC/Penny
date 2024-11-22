package app.penny.feature.newLedger

import androidx.compose.material3.SnackbarHostState
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.LedgerCover

data class NewLedgerUiState(
    val ledgerName: String = "",
    val isLoading: Boolean = false,
    val ledgerCover: LedgerCover = LedgerCover.DEFAULT,
    val ledgerDescription: String = "",
    val currency: Currency = Currency.USD,
    val currencySelectorModalVisible: Boolean = false,
    val errorMessage: String? = null,
    val ledgerCreateSuccess: Boolean? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
) {

}