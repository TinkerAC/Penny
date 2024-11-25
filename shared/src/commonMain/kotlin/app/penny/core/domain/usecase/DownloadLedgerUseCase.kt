package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import kotlinx.datetime.Instant

class DownloadLedgerUseCase(
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository

) {
    suspend operator fun invoke() {
        // 下载账本(未更新的)

        val lastSyncedAt = userDataRepository.getLastSyncedAt()

        // 下载账本

        val ledgers = ledgerRepository.downloadUnsyncedLedgers(lastSyncedAt ?: Instant.DISTANT_PAST)

        if (ledgers.isNotEmpty()) {
            // 处理下载成功,(插入或更新)
            ledgers.forEach {
                ledgerRepository.addLedger(it)
            }

            // 更新最后同步时间




        } else {




            // 处理下载失败

        }


    }
}