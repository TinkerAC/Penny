package app.penny.core.domain.usecase

import app.penny.core.data.model.toLedgerDto
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.UploadLedgerResponse
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant

class UploadUpdatedLedgersUseCase(
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository,
) {
    suspend operator fun invoke() {

        // 获取上次同步的时间
        val lastSyncedAt: Instant? = userDataRepository.getLastSyncedAt()

        // 获取在上次同步后更新的所有账本
        val ledgers: List<LedgerModel> = ledgerRepository.findLedgersUpdatedAfter(
            lastSyncedAt ?: Instant.DISTANT_PAST
        )

        if (ledgers.isEmpty()) {
            Logger.d("没有需要上传的账本。")
            return
        }

        try {

            // 上传账本
            val response: UploadLedgerResponse =
                ledgerRepository.uploadUnsyncedLedgers(
                    ledgers = ledgers,
                    lastSyncedAt = lastSyncedAt ?: Instant.DISTANT_PAST
                )

            when {
                response.success -> {
                    Logger.d("成功上传了 ${ledgers.size} 个账本。")
                    // 使用服务器响应更新最后同步时间
                    userDataRepository.setLastSyncedAt(
                        Instant.fromEpochSeconds(response.lastSyncedAt)
                    )
                }

                else -> {
                    Logger.e("上传 ${ledgers.size} 个账本失败")
                }
            }

        } catch (e: Exception) {
            Logger.e("上传账本失败：${e.message}")
            // 根据需要处理异常，例如重新抛出或执行其他操作
        }
    }
}