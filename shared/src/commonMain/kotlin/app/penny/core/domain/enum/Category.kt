package app.penny.core.domain.enum

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource

enum class Category(
    val parentCategory: Category?,
    val categoryName: StringResource,
    val categoryIcon: ImageVector,
    val description: String
) {
    // 顶级分类
    INCOME(
        null,
        SharedRes.strings.category_income,
        Icons.Filled.Grid4x4,
        "All types of income"
    ),
    EXPENSE(
        null,
        SharedRes.strings.category_expense,
        Icons.Filled.Grid4x4,
        "All types of expenses"
    ),

    // 支出一级分类 (Expense Primary Categories)
    EXPENSE_HOUSING(
        EXPENSE,
        SharedRes.strings.category_housing,
        Icons.Filled.Home,
        "Housing related expenses"
    ),
    EXPENSE_TRANSPORTATION(
        EXPENSE,
        SharedRes.strings.category_transportation,
        Icons.Filled.DirectionsCar,
        "Transportation related expenses"
    ),
    EXPENSE_FOOD(
        EXPENSE,
        SharedRes.strings.category_food,
        Icons.Filled.Fastfood,
        "Food and dining expenses"
    ),
    EXPENSE_HEALTH(
        EXPENSE,
        SharedRes.strings.category_health,
        Icons.Filled.HealthAndSafety,
        "Health and medical expenses"
    ),
    EXPENSE_PERSONAL_CARE(
        EXPENSE,
        SharedRes.strings.category_personal_care,
        Icons.Filled.Spa,
        "Personal care and grooming"
    ),
    EXPENSE_ENTERTAINMENT(
        EXPENSE,
        SharedRes.strings.category_entertainment,
        Icons.Filled.Movie,
        "Movies, music, games, and more"
    ),
    EXPENSE_EDUCATION(
        EXPENSE,
        SharedRes.strings.category_education,
        Icons.Filled.School,
        "Tuition and educational expenses"
    ),
    EXPENSE_INSURANCE(
        EXPENSE,
        SharedRes.strings.category_insurance,
        Icons.Filled.Shield,
        "Various insurance payments"
    ),
    EXPENSE_FINANCIAL(
        EXPENSE,
        SharedRes.strings.category_financial,
        Icons.Filled.AccountBalance,
        "Loan, credit, and investment expenses"
    ),
    EXPENSE_MISCELLANEOUS(
        EXPENSE,
        SharedRes.strings.category_miscellaneous,
        Icons.Filled.MoreHoriz,
        "Other general expenses"
    ),

    // 支出二级分类 - Housing
    EXPENSE_HOUSING_MORTGAGE(
        EXPENSE_HOUSING,
        SharedRes.strings.category_housing_mortgage_or_rent,
        Icons.Filled.Home,
        "Home mortgage or rent payments"
    ),
    EXPENSE_HOUSING_UTILITIES(
        EXPENSE_HOUSING,
        SharedRes.strings.category_housing_utilities,
        Icons.Filled.Power,
        "Electricity, water, gas, etc."
    ),
    EXPENSE_HOUSING_MAINTENANCE(
        EXPENSE_HOUSING,
        SharedRes.strings.category_housing_maintenance,
        Icons.Filled.Build,
        "Home repairs and maintenance"
    ),
    EXPENSE_HOUSING_PROPERTY_TAX(
        EXPENSE_HOUSING,
        SharedRes.strings.category_housing_property_tax,
        Icons.Filled.AccountBalance,
        "Annual property tax payments"
    ),
    EXPENSE_HOUSING_OTHER(
        EXPENSE_HOUSING,
        SharedRes.strings.category_housing_other,
        Icons.Filled.MoreHoriz,
        "Other housing expenses"
    ),

    // 支出二级分类 - Transportation
    EXPENSE_TRANSPORTATION_FUEL(
        EXPENSE_TRANSPORTATION,
        SharedRes.strings.category_transportation_fuel,
        Icons.Filled.LocalGasStation,
        "Gasoline or diesel for vehicles"
    ),
    EXPENSE_TRANSPORTATION_PUBLIC_TRANSPORT(
        EXPENSE_TRANSPORTATION,
        SharedRes.strings.category_transportation_public_transport,
        Icons.Filled.DirectionsBus,
        "Bus, train, subway fares"
    ),
    EXPENSE_TRANSPORTATION_PARKING(
        EXPENSE_TRANSPORTATION,
        SharedRes.strings.category_transportation_parking,
        Icons.Filled.LocalParking,
        "Parking fees and tolls"
    ),
    EXPENSE_TRANSPORTATION_VEHICLE_MAINTENANCE(
        EXPENSE_TRANSPORTATION,
        SharedRes.strings.category_transportation_vehicle_maintenance,
        Icons.Filled.Build,
        "Car repairs and servicing"
    ),
    EXPENSE_TRANSPORTATION_OTHER(
        EXPENSE_TRANSPORTATION,
        SharedRes.strings.category_transportation_other,
        Icons.Filled.MoreHoriz,
        "Other transportation expenses"
    ),

    // 支出二级分类 - Food
    EXPENSE_FOOD_GROCERIES(
        EXPENSE_FOOD,
        SharedRes.strings.category_food_groceries,
        Icons.Filled.ShoppingCart,
        "Household food purchases"
    ),
    EXPENSE_FOOD_DINING_OUT(
        EXPENSE_FOOD,
        SharedRes.strings.category_food_dining_out,
        Icons.Filled.Restaurant,
        "Restaurant and café expenses"
    ),
    EXPENSE_FOOD_SNACKS_COFFEE(
        EXPENSE_FOOD,
        SharedRes.strings.category_food_snacks_and_coffee,
        Icons.Filled.LocalCafe,
        "Quick bites and coffee runs"
    ),
    EXPENSE_FOOD_DELIVERY(
        EXPENSE_FOOD,
        SharedRes.strings.category_food_delivery,
        Icons.Filled.DeliveryDining,
        "Food delivery services"
    ),
    EXPENSE_FOOD_OTHER(
        EXPENSE_FOOD,
        SharedRes.strings.category_food_other,
        Icons.Filled.MoreHoriz,
        "Other food-related expenses"
    ),

    // 支出二级分类 - Health
    EXPENSE_HEALTH_INSURANCE(
        EXPENSE_HEALTH,
        SharedRes.strings.category_health_health_insurance,
        Icons.Filled.HealthAndSafety,
        "Health insurance premiums"
    ),
    EXPENSE_HEALTH_MEDICAL_BILLS(
        EXPENSE_HEALTH,
        SharedRes.strings.category_health_medical_bills,
        Icons.Filled.LocalHospital,
        "Doctor visits, hospital bills"
    ),
    EXPENSE_HEALTH_PHARMACY(
        EXPENSE_HEALTH,
        SharedRes.strings.category_health_pharmacy,
        Icons.Filled.LocalPharmacy,
        "Medications and supplements"
    ),
    EXPENSE_HEALTH_DENTAL_CARE(
        EXPENSE_HEALTH,
        SharedRes.strings.category_health_dental_care,
        Icons.Filled.EmojiPeople,
        "Dental check-ups and procedures"
    ),
    EXPENSE_HEALTH_OTHER(
        EXPENSE_HEALTH,
        SharedRes.strings.category_health_other,
        Icons.Filled.MoreHoriz,
        "Other health expenses"
    ),

    // 支出二级分类 - Personal Care
    EXPENSE_PERSONAL_CARE_CLOTHING(
        EXPENSE_PERSONAL_CARE,
        SharedRes.strings.category_personal_care_clothing,
        Icons.Filled.ShoppingBag,
        "Apparel and footwear"
    ),
    EXPENSE_PERSONAL_CARE_HAIR_BEAUTY(
        EXPENSE_PERSONAL_CARE,
        SharedRes.strings.category_personal_care_hair_and_beauty,
        Icons.Filled.ContentCut,
        "Salon, spa treatments"
    ),
    EXPENSE_PERSONAL_CARE_FITNESS(
        EXPENSE_PERSONAL_CARE,
        SharedRes.strings.category_personal_care_fitness,
        Icons.Filled.FitnessCenter,
        "Gym memberships, fitness classes"
    ),
    EXPENSE_PERSONAL_CARE_COSMETICS(
        EXPENSE_PERSONAL_CARE,
        SharedRes.strings.category_personal_care_cosmetics,
        Icons.Filled.Brush,
        "Makeup, skincare products"
    ),
    EXPENSE_PERSONAL_CARE_OTHER(
        EXPENSE_PERSONAL_CARE,
        SharedRes.strings.category_personal_care_other,
        Icons.Filled.MoreHoriz,
        "Other personal care expenses"
    ),

    // 支出二级分类 - Entertainment
    EXPENSE_ENTERTAINMENT_MOVIES_SHOWS(
        EXPENSE_ENTERTAINMENT,
        SharedRes.strings.category_entertainment_movies_and_shows,
        Icons.Filled.Movie,
        "Cinema tickets, theater shows"
    ),
    EXPENSE_ENTERTAINMENT_MUSIC_CONCERTS(
        EXPENSE_ENTERTAINMENT,
        SharedRes.strings.category_entertainment_music_and_concerts,
        Icons.Filled.MusicNote,
        "Concert tickets, music events"
    ),
    EXPENSE_ENTERTAINMENT_GAMES_APPS(
        EXPENSE_ENTERTAINMENT,
        SharedRes.strings.category_entertainment_games_and_apps,
        Icons.Filled.VideogameAsset,
        "Video games, mobile apps"
    ),
    EXPENSE_ENTERTAINMENT_STREAMING(
        EXPENSE_ENTERTAINMENT,
        SharedRes.strings.category_entertainment_streaming_services,
        Icons.Filled.Stream,
        "Monthly fees for Netflix, etc."
    ),
    EXPENSE_ENTERTAINMENT_OTHER(
        EXPENSE_ENTERTAINMENT,
        SharedRes.strings.category_entertainment_other,
        Icons.Filled.MoreHoriz,
        "Other entertainment expenses"
    ),

    // 支出二级分类 - Education
    EXPENSE_EDUCATION_TUITION(
        EXPENSE_EDUCATION,
        SharedRes.strings.category_education_tuition,
        Icons.Filled.School,
        "School and university fees"
    ),
    EXPENSE_EDUCATION_BOOKS_SUPPLIES(
        EXPENSE_EDUCATION,
        SharedRes.strings.category_education_books_and_supplies,
        Icons.Filled.Book,
        "Textbooks, stationery"
    ),
    EXPENSE_EDUCATION_ONLINE_COURSES(
        EXPENSE_EDUCATION,
        SharedRes.strings.category_education_online_courses,
        Icons.Filled.Computer,
        "E-learning and courses"
    ),
    EXPENSE_EDUCATION_STUDENT_LOANS(
        EXPENSE_EDUCATION,
        SharedRes.strings.category_education_student_loans,
        Icons.Filled.AccountBalance,
        "Student loan repayments"
    ),
    EXPENSE_EDUCATION_OTHER(
        EXPENSE_EDUCATION,
        SharedRes.strings.category_education_other,
        Icons.Filled.MoreHoriz,
        "Other educational expenses"
    ),

    // 支出二级分类 - Insurance
    EXPENSE_INSURANCE_AUTO_INSURANCE(
        EXPENSE_INSURANCE,
        SharedRes.strings.category_insurance_auto_insurance,
        Icons.Filled.DirectionsCar,
        "Car insurance premiums"
    ),
    EXPENSE_INSURANCE_HEALTH_INSURANCE(
        EXPENSE_INSURANCE,
        SharedRes.strings.category_insurance_health_insurance,
        Icons.Filled.HealthAndSafety,
        "Health insurance premiums"
    ),
    EXPENSE_INSURANCE_HOME_INSURANCE(
        EXPENSE_INSURANCE,
        SharedRes.strings.category_insurance_home_insurance,
        Icons.Filled.Home,
        "Homeowner’s insurance"
    ),
    EXPENSE_INSURANCE_LIFE_INSURANCE(
        EXPENSE_INSURANCE,
        SharedRes.strings.category_insurance_life_insurance,
        Icons.Filled.Favorite,
        "Life insurance premiums"
    ),
    EXPENSE_INSURANCE_OTHER(
        EXPENSE_INSURANCE,
        SharedRes.strings.category_insurance_other,
        Icons.Filled.MoreHoriz,
        "Other insurance expenses"
    ),

    // 支出二级分类 - Financial
    EXPENSE_FINANCIAL_LOAN_PAYMENTS(
        EXPENSE_FINANCIAL,
        SharedRes.strings.category_financial_loan_payments,
        Icons.Filled.AttachMoney,
        "Mortgage, personal loan repayments"
    ),
    EXPENSE_FINANCIAL_CREDIT_CARD_PAYMENTS(
        EXPENSE_FINANCIAL,
        SharedRes.strings.category_financial_credit_card_payments,
        Icons.Filled.CreditCard,
        "Credit card bill payments"
    ),
    EXPENSE_FINANCIAL_INVESTMENTS(
        EXPENSE_FINANCIAL,
        SharedRes.strings.category_financial_investments,
        Icons.Filled.TrendingUp,
        "Stocks, bonds, and other investments"
    ),
    EXPENSE_FINANCIAL_BANKING_FEES(
        EXPENSE_FINANCIAL,
        SharedRes.strings.category_financial_banking_fees,
        Icons.Filled.AccountBalance,
        "Bank account and ATM fees"
    ),
    EXPENSE_FINANCIAL_OTHER(
        EXPENSE_FINANCIAL,
        SharedRes.strings.category_financial_other,
        Icons.Filled.MoreHoriz,
        "Other financial expenses"
    ),

    // 支出二级分类 - Miscellaneous
    EXPENSE_MISCELLANEOUS_GIFTS(
        EXPENSE_MISCELLANEOUS,
        SharedRes.strings.category_miscellaneous_gifts,
        Icons.Filled.TagFaces,
        "Presents for others"
    ),
    EXPENSE_MISCELLANEOUS_DONATIONS(
        EXPENSE_MISCELLANEOUS,
        SharedRes.strings.category_miscellaneous_donations,
        Icons.Filled.Favorite,
        "Charitable contributions"
    ),
    EXPENSE_MISCELLANEOUS_SUBSCRIPTIONS(
        EXPENSE_MISCELLANEOUS,
        SharedRes.strings.category_miscellaneous_subscriptions,
        Icons.Filled.Subscriptions,
        "Monthly or yearly service fees"
    ),
    EXPENSE_MISCELLANEOUS_PET_CARE(
        EXPENSE_MISCELLANEOUS,
        SharedRes.strings.category_miscellaneous_pet_care,
        Icons.Filled.Pets,
        "Pet food, vet bills"
    ),
    EXPENSE_MISCELLANEOUS_OTHER(
        EXPENSE_MISCELLANEOUS,
        SharedRes.strings.category_miscellaneous_other,
        Icons.Filled.MoreHoriz,
        "Other miscellaneous expenses"
    ),

    // 收入一级分类 (Income Primary Categories)
    INCOME_SALARY(
        INCOME,
        SharedRes.strings.category_salary,
        Icons.Filled.AccountBalance,
        "Regular salary and wages"
    ),
    INCOME_BUSINESS(
        INCOME,
        SharedRes.strings.category_business,
        Icons.Filled.Store,
        "Business or self-employment income"
    ),
    INCOME_INVESTMENT(
        INCOME,
        SharedRes.strings.category_investment,
        Icons.Filled.TrendingUp,
        "Income from investments"
    ),
    INCOME_FREELANCE(
        INCOME,
        SharedRes.strings.category_freelance,
        Icons.Filled.Work,
        "Income from freelance work"
    ),
    INCOME_MISCELLANEOUS(
        INCOME,
        SharedRes.strings.category_income_miscellaneous,
        Icons.Filled.MoreHoriz,
        "Other sources of income"
    ),

    // 收入二级分类 - Salary
    INCOME_SALARY_BASIC_SALARY(
        INCOME_SALARY,
        SharedRes.strings.category_salary_basic_salary,
        Icons.Filled.AttachMoney,
        "Base salary income"
    ),
    INCOME_SALARY_BONUS(
        INCOME_SALARY,
        SharedRes.strings.category_salary_bonus,
        Icons.Filled.Star,
        "Performance-based bonuses"
    ),
    INCOME_SALARY_OVERTIME(
        INCOME_SALARY,
        SharedRes.strings.category_salary_overtime,
        Icons.Filled.AccessTime,
        "Overtime pay"
    ),
    INCOME_SALARY_COMMISSIONS(
        INCOME_SALARY,
        SharedRes.strings.category_salary_commissions,
        Icons.Filled.MonetizationOn,
        "Sales or performance commissions"
    ),
    INCOME_SALARY_OTHER(
        INCOME_SALARY,
        SharedRes.strings.category_salary_other,
        Icons.Filled.MoreHoriz,
        "Other salary-related income"
    ),

    // 收入二级分类 - Business
    INCOME_BUSINESS_SALES(
        INCOME_BUSINESS,
        SharedRes.strings.category_business_sales,
        Icons.Filled.ShoppingCart,
        "Income from product sales"
    ),
    INCOME_BUSINESS_SERVICES(
        INCOME_BUSINESS,
        SharedRes.strings.category_business_services,
        Icons.Filled.Build,
        "Income from provided services"
    ),
    INCOME_BUSINESS_ROYALTIES(
        INCOME_BUSINESS,
        SharedRes.strings.category_business_royalties,
        Icons.Filled.MonetizationOn,
        "Royalty income"
    ),
    INCOME_BUSINESS_OTHER(
        INCOME_BUSINESS,
        SharedRes.strings.category_business_other,
        Icons.Filled.MoreHoriz,
        "Other business income"
    ),

    // 收入二级分类 - Investment
    INCOME_INVESTMENT_DIVIDENDS(
        INCOME_INVESTMENT,
        SharedRes.strings.category_investment_dividends,
        Icons.Filled.PieChart,
        "Dividend income from stocks"
    ),
    INCOME_INVESTMENT_INTEREST(
        INCOME_INVESTMENT,
        SharedRes.strings.category_investment_interest,
        Icons.Filled.Interests,
        "Interest from savings or bonds"
    ),
    INCOME_INVESTMENT_CAPITAL_GAINS(
        INCOME_INVESTMENT,
        SharedRes.strings.category_investment_capital_gains,
        Icons.AutoMirrored.Filled.TrendingUp,
        "Profit from selling investments"
    ),
    INCOME_INVESTMENT_RENTAL_INCOME(
        INCOME_INVESTMENT,
        SharedRes.strings.category_investment_rental_income,
        Icons.Filled.Home,
        "Income from renting properties"
    ),
    INCOME_INVESTMENT_OTHER(
        INCOME_INVESTMENT,
        SharedRes.strings.category_investment_other,
        Icons.Filled.MoreHoriz,
        "Other investment income"
    ),

    // 收入二级分类 - Freelance
    INCOME_FREELANCE_PROJECTS(
        INCOME_FREELANCE,
        SharedRes.strings.category_freelance_projects,
        Icons.Filled.Work,
        "Project-based freelance work"
    ),
    INCOME_FREELANCE_CONSULTING(
        INCOME_FREELANCE,
        SharedRes.strings.category_freelance_consulting,
        Icons.Filled.BusinessCenter,
        "Consulting or advisory services"
    ),
    INCOME_FREELANCE_TIPS(
        INCOME_FREELANCE,
        SharedRes.strings.category_freelance_tips,
        Icons.Filled.AttachMoney,
        "Gratuities from clients"
    ),
    INCOME_FREELANCE_OTHER(
        INCOME_FREELANCE,
        SharedRes.strings.category_freelance_other,
        Icons.Filled.MoreHoriz,
        "Other freelance income"
    ),

    // 收入二级分类 - Miscellaneous
    INCOME_MISCELLANEOUS_GIFTS(
        INCOME_MISCELLANEOUS,
        SharedRes.strings.category_income_miscellaneous_gifts,
        Icons.Filled.TagFaces,
        "Monetary gifts received"
    ),
    INCOME_MISCELLANEOUS_GOVERNMENT_BENEFITS(
        INCOME_MISCELLANEOUS,
        SharedRes.strings.category_income_miscellaneous_government_benefits,
        Icons.Filled.AccountBalance,
        "Social security, unemployment benefits"
    ),
    INCOME_MISCELLANEOUS_LOTTERY_WINNINGS(
        INCOME_MISCELLANEOUS,
        SharedRes.strings.category_income_miscellaneous_lottery_winnings,
        Icons.Filled.Casino,
        "Income from lotteries or gambling"
    ),
    INCOME_MISCELLANEOUS_OTHER(
        INCOME_MISCELLANEOUS,
        SharedRes.strings.category_income_miscellaneous_other,
        Icons.Filled.MoreHoriz,
        "Other miscellaneous income"
    );


    // Helper functions
    fun getLevel1Category(): Category {
        var currentCategory = this
        while (currentCategory.parentCategory != null && currentCategory.parentCategory != INCOME && currentCategory.parentCategory != EXPENSE) {
            currentCategory = currentCategory.parentCategory!!
        }
        return currentCategory
    }

    fun getLevel(): Int {
        var level = 0
        var parent = this.parentCategory
        while (parent != null) {
            level++
            parent = parent.parentCategory
        }
        return level
    }

    companion object {

        // 获取特定父类的所有子类
        fun getSubCategories(parentCategory: Category): List<Category> {
            return entries.filter { it.parentCategory == parentCategory }
        }

        // 根据名称获取分类
        fun fromCategoryName(name: String): Category? {
            return entries.find {
                it.name == name
            }
        }

        fun getLevel(category: Category): Int {
            var level = 0
            var parent = category.parentCategory
            while (parent != null) {
                level++
                parent = parent.parentCategory
            }
            return level
        }


        /**
         * return all level 1 Income categories
         */
        fun getIncomeCategories(): List<Category> {
            return getSubCategories(INCOME)
        }

        /**
         * return all level 1 Expense categories
         */
        fun getExpenseCategories(): List<Category> {
            return getSubCategories(EXPENSE)
        }

        /**
         * return all level 1 categories
         */
        fun getLevel1Categories(): List<Category> {
            return getIncomeCategories() + getExpenseCategories()
        }

        /**
         * return all level 2 categories
         */
        fun getLevel2Categories(): List<Category> {
            return getLevel1Categories().flatMap { getSubCategories(it) }
        }

        /**
         * 根据所有分类动态生成一个用于LLM的提示词框架，帮助LLM根据自然语言从中提取结构化信息。
         */
        fun generateLLMPrompt(
            text: String,
            userLocalDate: String
        ): String {
            val incomeCategories = getSubCategories(INCOME).joinToString("\n") { parent ->
                val subs =
                    getSubCategories(parent).joinToString(", ") { it.name }  // 使用 name 代替 categoryName
                if (subs.isBlank()) parent.name else "${parent.name}: $subs"  // 使用 name 代替 categoryName
            }

            val expenseCategories = getSubCategories(EXPENSE).joinToString("\n") { parent ->
                val subs =
                    getSubCategories(parent).joinToString(", ") { it.name }  // 使用 name 代替 categoryName
                if (subs.isBlank()) parent.name else "${parent.name}: $subs"  // 使用 name 代替 categoryName
            }
            return """
[Role]
You are a helpful assistant that extracts structured information from a user's natural language description of a financial transaction. 
[Goal]
The user will provide a text describing a transaction and a user's local date.
You will identify:
   - amount (The transaction amount, if any)
   - remark (Any free-text note or remark about the transaction)
   - category (The transaction category, chosen from the provided category list)
   - transactionType (Either "income" or "expense" based on context)
   - transactionDate (A specific date in yyyy-MM-dd format if mentioned ,or inferred from the user's local date and user's description)

Here are the categories you can choose from:
INCOME CATEGORIES:
$incomeCategories

EXPENSE CATEGORIES:
$expenseCategories

[Additional Information]
The output should be a JSON object with the fields shown above.
1.If you cannot find a suitable category from the user's description, category can be null.
3.If can't identify the transaction date, use the user's local date as the transaction date.
2.If information is missing or not provided in the user's input, set that field to null.


Return only the JSON object and nothing else.
[Example]
INPUT: 
text:"I spent $50 on groceries on 15th November"
userLocalDate: 2024-11-20

in this case, user provided accurate transaction date(11-15), so use that date in the output,
and output should be:
{
    "amount": 50,
    "remark": "on groceries",
    "category": "EXPENSE_FOOD_GROCERIES",
    "transactionType": "EXPENSE",
    "transactionDate": "2024-11-15",
}

INPUT:
text:"It cost me $100 to fix my car yesterday"
userLocalDate: 2024-12-20

in this case, user provided relative transaction date(yesterday), so use userLocalDate to calculate the transaction date(12-19),
and output should be:
{
    "amount": 100,
    "remark": "to fix my car",
    "category": "EXPENSE_TRANSPORTATION_VEHICLE_MAINTENANCE",
    "transactionType": "EXPENSE",
    "transactionDate": "2024-12-19",
}


INPUT:
text:"I received $500 as a gift"
userLocalDate: 2024-12-25

in this case, user didn't provide transaction date, so use userLocalDate as the transaction date(12-25),
and the output should be:
{
    "amount": 500,
    "remark": "as a gift",
    "category": "INCOME_MISCELLANEOUS_GIFTS",
    "transactionType": "INCOME",
    "transactionDate": "2024-12-25",
}


[NOTE]:keep in mind to return only the JSON object and nothing else.
     """.trimIndent()
        }
    }


    override fun toString(): String {
        return super.toString()
    }

}




