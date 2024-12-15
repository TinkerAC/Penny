package app.penny.feature.newLedger

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class NewLedgerUiEvent {
    data class ShowSnackBar(val message: String) : NewLedgerUiEvent()
    data class OnFinishInsert @OptIn(ExperimentalUuidApi::class) constructor(val newLedgerUuid: Uuid) :
        NewLedgerUiEvent()
}
