package app.penny.core.domain.model

import app.penny.core.domain.enumerate.Category
import app.penny.feature.transactions.GroupByType

/**
 * 分组标识，用于区分“时间分组”和“类别分组”等
 */
sealed class GroupIdentifier {
    /**
     * 返回统一的“展示字符串”
     */
    abstract val displayString: String

    /**
     * 时间分组标识
     *
     * @param groupOption 同样区分是Day, Week, Month, Season, Year
     * @param year 对于所有Time分组都至少包含年份
     * @param month 用于Month/Day/Season等情况
     * @param day 用于Day
     * @param weekOfYear 用于Week
     * @param quarter 用于Season
     */
    data class TimeGroupIdentifier(
        val groupOption: GroupByType.Time.GroupOption,
        val year: Int,
        val month: Int? = null,
        val day: Int? = null,
        val weekOfYear: Int? = null,
        val quarter: Int? = null,
    ) : GroupIdentifier() {


        // 根据 groupOption 构建对应的展示文案
        override val displayString: String
            get() = when (groupOption) {
                GroupByType.Time.GroupOption.Day -> {
                    // 2024-12-01
                    year.toString().padStart(4, '0') +
                            "-${month?.toString()?.padStart(2, '0')}" +
                            "-${day?.toString()?.padStart(2, '0')}"
                }

                GroupByType.Time.GroupOption.Week -> {
                    // 2024-W02
                    year.toString().padStart(4, '0') +
                            "-W${weekOfYear?.toString()?.padStart(2, '0')}"
                }

                GroupByType.Time.GroupOption.Month -> {
                    // 2024-12
                    year.toString().padStart(4, '0') +
                            "-${month?.toString()?.padStart(2, '0')}"
                }

                GroupByType.Time.GroupOption.Season -> {
                    // 2024-Q1
                    year.toString().padStart(4, '0') +
                            "-Q${quarter}"
                }

                GroupByType.Time.GroupOption.Year -> {
                    // 2024
                    year.toString().padStart(4, '0')
                }
            }






    }

    /**
     * 类别分组标识
     *
     * @param groupOption 区分 Primary、Secondary
     * @param category 数据库中的分类枚举
     */
    data class CategoryGroupIdentifier(
        val groupOption: GroupByType.Category.GroupOption,
        val category: Category
    ) : GroupIdentifier() {

        override val displayString: String
            get() = category.categoryName.toString()
    }

}