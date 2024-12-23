package app.penny.feature.ledgerDetail

import dev.icerock.moko.resources.StringResource

sealed class LedgerDetailUiEvent {
    data class ShowSnackBar(val message: StringResource) : LedgerDetailUiEvent()
}

