package app.penny.feature.newLedger

import app.penny.core.domain.model.LedgerModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class NewLedgerUiEvent {
    data class ShowSnackBar(val message: String) : NewLedgerUiEvent()
    data class OnFinishInsert(val newLedger: LedgerModel) :
        NewLedgerUiEvent()
}
