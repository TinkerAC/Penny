package app.penny.data.repository

import app.penny.domain.model.LedgerModel

interface LedgerRepository {
    suspend fun insertLedger(ledgerModel: LedgerModel)
    suspend fun getLedgerById(ledgerId: Long): LedgerModel
    suspend fun getAllLedgers(): List<LedgerModel>
    suspend fun updateLedger(ledgerModel: LedgerModel)
    suspend fun deleteLedger(ledgerId: Long)
}