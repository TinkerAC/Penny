package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.model.TransactionModel
import co.touchlab.kermit.Logger
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class SearchTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        ledgerUuid:Uuid,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TransactionModel> {
        val transactionsByLedger = transactionRepository.findByLedgerUuid(ledgerUuid)


        //the timeStamp of 00:00:00 of the startDate
        val startTimeStamp = startDate.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds

        //the timeStamp of 23:59:59 of the endDate
        val endTimeStamp = endDate.plus(DatePeriod(days = 1))
            .atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds - 1


        val result = transactionsByLedger.filter {
            it.transactionInstant.epochSeconds in startTimeStamp..endTimeStamp
        }
        Logger.d("Get transactions by ledger $ledgerUuid between $startDate and $endDate: ${result.size}")
        return result
    }
}