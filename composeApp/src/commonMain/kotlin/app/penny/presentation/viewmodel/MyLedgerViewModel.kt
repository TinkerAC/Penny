package app.penny.presentation.viewmodel

import app.penny.domain.usecase.InsertLedgerUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch


class MyLedgerViewModel(
    private val insertLedgerUseCase: InsertLedgerUseCase
) : ScreenModel {



}