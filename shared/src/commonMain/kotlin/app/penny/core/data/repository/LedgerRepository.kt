package app.penny.core.data.repository

import app.penny.core.domain.model.LedgerModel

interface LedgerRepository {
    suspend fun insertLedger(ledgerModel: LedgerModel)
    suspend fun getLedgerById(ledgerId: Long): LedgerModel
    suspend fun getAllLedgers(): List<LedgerModel>
    suspend fun updateLedger(ledgerModel: LedgerModel)
    suspend fun deleteLedger(ledgerId: Long)
}