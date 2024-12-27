// File: shared/src/commonMain/kotlin/app/penny/core/domain/model/valueobject/YearMonth.kt
package app.penny.core.domain.model.valueObject

import app.penny.core.utils.getDaysInMonth
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

/**
 * YearMonth value object representing a specific year and month.
 */
@Serializable
data class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {
    init {
        require(month in 1..12) {
            "Month must be between 1 and 12,given year $year , month $month"
        }
    }

    companion object {
        /**
         * Creates a YearMonth instance from the current date.
         */
        fun now(): YearMonth {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return YearMonth(now.year, now.monthNumber)
        }

        /**
         * Creates a YearMonth instance from a string in the format "YYYY-MM".
         */
        fun fromString(yearMonth: String): YearMonth {
            val parts = yearMonth.split("-")
            require(parts.size == 2) { "Incorrect string format, should be 'YYYY-MM'." }
            val year = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid year.")
            val month =
                parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid month.")
            return YearMonth(year, month)
        }
    }

    /**
     * Gets the start Instant of the month (first day at 00:00:00).
     *
     * @param timeZone The time zone to use for conversion. Defaults to the system's current time zone.
     * @return The Instant representing the start of the month.
     */
    fun getStartInstant(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
        val startDateTime = LocalDateTime(year, month, 1, 0, 0, 0, 0)
        return startDateTime.toInstant(timeZone)
    }

    /**
     * Gets the end Instant of the month (last day at 23:59:59.999999999).
     *
     * @param timeZone The time zone to use for conversion. Defaults to the system's current time zone.
     * @return The Instant representing the end of the month.
     */
    fun getEndInstant(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
        //days in month
        val daysInMonth = getDaysInMonth(this)
        // 创建当前月份最后一天的LocalDateTime
        val endDateTime = LocalDateTime(year, month, daysInMonth, 23, 59, 59, 999_999_999)

        return endDateTime.toInstant(timeZone)
    }

    /**
     * Gets the start epoch seconds of the month.
     *
     * @param timeZone The time zone to use for conversion. Defaults to the system's current time zone.
     * @return The epoch seconds representing the start of the month.
     */
    fun getStartEpochSeconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        return getStartInstant(timeZone).epochSeconds
    }

    /**
     * Gets the end epoch seconds of the month.
     *
     * @param timeZone The time zone to use for conversion. Defaults to the system's current time zone.
     * @return The epoch seconds representing the end of the month.
     */
    fun getEndEpochSeconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        return getEndInstant(timeZone).epochSeconds
    }

    /**
     * Gets the range of epoch seconds for the start and end of the month.
     *
     * @param timeZone The time zone to use for conversion. Defaults to the system's current time zone.
     * @return A Pair containing the start and end epoch seconds of the month.
     */
    fun getEpochSecondsRange(timeZone: TimeZone = TimeZone.currentSystemDefault()): Pair<Long, Long> {
        return Pair(getStartEpochSeconds(timeZone), getEndEpochSeconds(timeZone))
    }

    /**
     * Converts to LocalDate, defaulting to the first day of the month.
     *
     * @return The LocalDate representing the first day of the month.
     */
    fun toLocalDate(): LocalDate {
        return LocalDate(year, month, 1)
    }

    /**
     * Adds or subtracts months from the current YearMonth.
     *
     * @param offset The number of months to add (positive) or subtract (negative).
     * @return A new YearMonth instance with the adjusted month and year.
     */
    fun plusMonths(offset: Long): YearMonth {
        val totalMonths = year * 12 + (month - 1) + offset
        val newYear = (totalMonths / 12).toInt()
        val newMonth =
            ((totalMonths % 12) + 12) % 12 + 1 // Ensures the month is between 1 and 12
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
