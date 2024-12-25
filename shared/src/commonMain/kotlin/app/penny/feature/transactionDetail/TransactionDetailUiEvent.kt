package app.penny.feature.transactionDetail

import app.penny.core.domain.model.LedgerModel
import dev.icerock.moko.resources.StringResource


sealed class TransactionDetailUiEvent {
    data class ShowSnackBar(val message: StringResource) : TransactionDetailUiEvent()
    data class FocusAmountInput(val focus: Boolean) : TransactionDetailUiEvent()
}
