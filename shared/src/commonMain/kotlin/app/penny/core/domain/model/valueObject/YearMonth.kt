// File: shared/src/commonMain/kotlin/app/penny/core/domain/model/valueobject/YearMonth.kt
package app.penny.core.domain.model.valueObject

import kotlinx.serialization.Serializable
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate

/**
 * YearMonth 值对象，表示某年的某个月。
 */
@Serializable
data class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {
    init {
        require(month in 1..12) { "月份必须在1到12之间" }
    }

    companion object {
        /**
         * 从当前日期创建 YearMonth 实例。
         */
        fun now(): YearMonth {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return YearMonth(now.year, now.monthNumber)
        }

        /**
         * 从字符串（格式：YYYY-MM）创建 YearMonth 实例。
         */
        fun fromString(yearMonth: String): YearMonth {
            val parts = yearMonth.split("-")
            require(parts.size == 2) { "字符串格式不正确，应为 'YYYY-MM'" }
            val year = parts[0].toIntOrNull() ?: throw IllegalArgumentException("年份无效")
            val month = parts[1].toIntOrNull() ?: throw IllegalArgumentException("月份无效")
            return YearMonth(year, month)
        }
    }

    /**
     * 转换为 LocalDate，默认设为当月的第一天。
     */
    fun toLocalDate(): LocalDate {
        return LocalDate(year, month, 1)
    }

    /**
     * 增加或减少月份
     * @param offset 偏移量，正数为增加，负数为减少
     */
    fun plusMonths(offset: Long): YearMonth {
        val totalMonths = year * 12 + (month - 1) + offset
        val newYear = (totalMonths / 12).toInt()
        val newMonth = ((totalMonths % 12) + 12) % 12 + 1 // 确保月份在1到12之间
        return YearMonth(newYear, newMonth.toInt())
    }

    override fun compareTo(other: YearMonth): Int {
        return if (year != other.year) {
            year.compareTo(other.year)
        } else {
            month.compareTo(other.month)
        }
    }

    override fun toString(): String {
        return "$year-${month.toString().padStart(2, '0')}"
    }
}
