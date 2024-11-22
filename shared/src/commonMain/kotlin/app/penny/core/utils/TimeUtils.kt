// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/transactions/utils/DateUtils.kt
package app.penny.core.utils

import app.penny.feature.analytics.YearMonth
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

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


/**
 * 获取指定年份和月份的天数
 *
 * @param year 年份，例如 2023
 * @param month 月份（1-12）
 * @return 该月的天数
 */
fun getDaysInMonth(year: Int, month: Int): Int {
    // 创建指定月份的第一天
    val date = LocalDate(year, month, 1)

    // 计算下一个月的第一天
    val nextMonth = if (month == 12) {
        // 如果是12月，下一个月为下一年的1月
        LocalDate(year + 1, 1, 1)
    } else {
        // 否则，下一个月为当前年的下一个月
        LocalDate(year, month + 1, 1)
    }

    // 通过减去一天，得到当前月的最后一天
    val lastDayOfMonth = nextMonth.minus(DatePeriod(days = 1))

    // 返回该月的天数
    return lastDayOfMonth.dayOfMonth
}


fun generateDateSequence(
    startDate: LocalDate,
    endDate: LocalDate,
    stepDay: Int = 1
): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var currentDate = startDate
    while (currentDate <= endDate) {
        dates.add(currentDate)
        currentDate = currentDate.plus(DatePeriod(days = stepDay))
    }
    return dates
}

fun generateMonthSequence(startDate: LocalDate, endDate: LocalDate): List<YearMonth> {
    val months = mutableListOf<YearMonth>()
    var currentMonth = YearMonth(startDate.year, startDate.monthNumber)
    while (currentMonth <= YearMonth(endDate.year, endDate.monthNumber)) {
        months.add(currentMonth)
        currentMonth = if (currentMonth.month == 12) {
            YearMonth(currentMonth.year + 1, 1)
        } else {
            YearMonth(currentMonth.year, currentMonth.month + 1)
        }
    }
    return months
}