// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/transactions/utils/DateUtils.kt
package app.penny.utils
import kotlinx.datetime.LocalDate
import app.penny.presentation.ui.screens.transactions.GroupBy
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.*
import kotlin.math.floor
object TimeUtils {

    /**
     * 将时间戳转换为 LocalDateTime
     */
    fun toLocalDateTime(epochMilliseconds: Long): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(epochMilliseconds)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }



}


fun localDateNow(): LocalDate {
    val now = Clock.System.now()
    now.toLocalDateTime(TimeZone.currentSystemDefault())
    return now.toLocalDateTime(TimeZone.currentSystemDefault()).date

}

