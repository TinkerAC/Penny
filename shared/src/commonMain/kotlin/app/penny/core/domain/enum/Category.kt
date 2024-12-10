package app.penny.core.domain.enum

import androidx.compose.runtime.TestOnly


enum class Category(
    val parentCategory: Category?,
    val categoryName: String,
    val categoryIcon: String,
    val description: String
) {
    // 顶级类别
    INCOME(null, "Income", "ic_category_income", "All types of income"),
    EXPENSE(null, "Expense", "ic_category_expense", "All types of expenses"),

    // ------------------- 收入子类别 -------------------
    // 1. Salary & Wages
    SALARY_WAGES(
        INCOME,
        "Salary & Wages",
        "ic_category_salary_wages",
        "Income from regular employment"
    ),
    BASE_SALARY(SALARY_WAGES, "Base Salary", "ic_base_salary", "Main salary from your job"),
    BONUS(SALARY_WAGES, "Bonus", "ic_bonus", "Additional performance-based pay"),
    OVERTIME(SALARY_WAGES, "Overtime", "ic_overtime", "Extra pay for overtime work"),
    COMMISSION(
        SALARY_WAGES,
        "Commission",
        "ic_commission",
        "Income based on sales or performance metrics"
    ),

    // 2. Business & Freelance
    BUSINESS_FREELANCE(
        INCOME,
        "Business & Freelance",
        "ic_category_business_freelance",
        "Income from side business or freelance work"
    ),
    FREELANCE(
        BUSINESS_FREELANCE,
        "Freelance/Gig Work",
        "ic_freelance",
        "Payment for freelance or gig economy jobs"
    ),
    CONSULTING_FEES(
        BUSINESS_FREELANCE,
        "Consulting Fees",
        "ic_consulting",
        "Fees for providing professional advice"
    ),
    SIDE_BUSINESS_INCOME(
        BUSINESS_FREELANCE,
        "Side Business Income",
        "ic_side_business_income",
        "Earnings from a small business or side hustle"
    ),

    // 3. Investments & Passive Income
    INVESTMENTS_PASSIVE(
        INCOME,
        "Investments & Passive Income",
        "ic_category_investments_passive",
        "Income from investments or passive sources"
    ),
    DIVIDENDS(INVESTMENTS_PASSIVE, "Dividends", "ic_dividends", "Payments from stocks or funds"),
    INTEREST(
        INVESTMENTS_PASSIVE,
        "Interest",
        "ic_interest",
        "Interest from savings, bonds, or loans"
    ),
    RENTAL_INCOME(
        INVESTMENTS_PASSIVE,
        "Rental Income",
        "ic_rental_income",
        "Income from renting out property"
    ),

    // 4. Government & Benefits
    GOVERNMENT_BENEFITS(
        INCOME,
        "Government & Benefits",
        "ic_category_government_benefits",
        "Income from government programs"
    ),
    TAX_REFUNDS(
        GOVERNMENT_BENEFITS,
        "Tax Refunds",
        "ic_tax_refunds",
        "Reimbursements from paid taxes"
    ),
    UNEMPLOYMENT_BENEFITS(
        GOVERNMENT_BENEFITS,
        "Unemployment Benefits",
        "ic_unemployment_benefits",
        "Payments received while unemployed"
    ),
    SOCIAL_SECURITY(
        GOVERNMENT_BENEFITS,
        "Social Security",
        "ic_social_security",
        "Government old-age or disability benefits"
    ),
    PENSION(GOVERNMENT_BENEFITS, "Pension", "ic_pension", "Retirement income from a pension plan"),

    // 5. Miscellaneous & Other
    MISC_OTHER_INCOME(
        INCOME,
        "Miscellaneous & Other",
        "ic_category_misc_other_income",
        "Other forms of income not covered above"
    ),
    GIFTS_RECEIVED(
        MISC_OTHER_INCOME,
        "Gifts Received",
        "ic_gifts_received",
        "Money received as a gift"
    ),
    LOTTERY_WINNINGS(
        MISC_OTHER_INCOME,
        "Lottery Winnings",
        "ic_lottery_winnings",
        "Money won from lotteries or gambling"
    ),
    INHERITANCE(
        MISC_OTHER_INCOME,
        "Inheritance",
        "ic_inheritance",
        "Wealth received from an estate"
    ),

    // ------------------- 支出子类别 -------------------
    // 1. Housing
    HOUSING(EXPENSE, "Housing", "ic_category_housing", "Expenses related to your home"),
    RENT_MORTGAGE(
        HOUSING,
        "Rent/Mortgage",
        "ic_rent_mortgage",
        "Monthly rent or mortgage payments"
    ),
    PROPERTY_TAXES(
        HOUSING,
        "Property Taxes",
        "ic_property_taxes",
        "Taxes related to property ownership"
    ),
    HOME_MAINTENANCE(
        HOUSING,
        "Home Maintenance & Repairs",
        "ic_home_maintenance",
        "Expenses for home upkeep"
    ),
    HOME_INSURANCE(HOUSING, "Home Insurance", "ic_home_insurance", "Insurance covering your home"),

    // 2. Utilities
    UTILITIES(EXPENSE, "Utilities", "ic_category_utilities", "Basic services for your home"),
    ELECTRICITY(UTILITIES, "Electricity", "ic_electricity", "Monthly electric bill"),
    WATER(UTILITIES, "Water", "ic_water", "Water and sewer charges"),
    GAS(UTILITIES, "Gas", "ic_gas", "Natural gas for heating or cooking"),
    INTERNET_PHONE_CABLE(
        UTILITIES,
        "Internet/Phone/Cable",
        "ic_internet_phone_cable",
        "Telecommunication and media services"
    ),

    // 3. Transportation
    TRANSPORTATION(EXPENSE, "Transportation", "ic_category_transportation", "Costs to get around"),
    FUEL(TRANSPORTATION, "Fuel", "ic_fuel", "Gasoline or diesel for vehicles"),
    AUTO_INSURANCE_EXPENSE(
        TRANSPORTATION,
        "Auto Insurance",
        "ic_auto_insurance_expense",
        "Insurance for your vehicle"
    ),
    AUTO_MAINTENANCE_EXPENSE(
        TRANSPORTATION,
        "Auto Maintenance",
        "ic_auto_maintenance_expense",
        "Repair and maintenance costs for your vehicle"
    ),
    PUBLIC_TRANSIT(
        TRANSPORTATION,
        "Public Transit",
        "ic_public_transit",
        "Bus, train, or subway fares"
    ),
    RIDE_SHARING(
        TRANSPORTATION,
        "Ride-Sharing",
        "ic_ride_sharing",
        "Uber, Lyft, or other ride-hailing services"
    ),
    PARKING(TRANSPORTATION, "Parking", "ic_parking", "Parking fees"),
    AIR_TRAVEL(TRANSPORTATION, "Air Travel", "ic_air_travel", "Airline tickets and related fees"),

    // 4. Groceries & Household Supplies
    GROCERIES_HOUSEHOLD(
        EXPENSE,
        "Groceries & Household Supplies",
        "ic_category_groceries_household",
        "Food and basic household items"
    ),
    GROCERIES(GROCERIES_HOUSEHOLD, "Groceries", "ic_groceries", "Everyday food shopping"),
    CLEANING_SUPPLIES(
        GROCERIES_HOUSEHOLD,
        "Cleaning Supplies",
        "ic_cleaning_supplies",
        "Detergents, paper towels, etc."
    ),
    HOUSEHOLD_ITEMS(
        GROCERIES_HOUSEHOLD,
        "Household Items",
        "ic_household_items",
        "Non-food household necessities"
    ),

    // 5. Dining & Entertainment
    DINING_ENTERTAINMENT(
        EXPENSE,
        "Dining & Entertainment",
        "ic_category_dining_entertainment",
        "Meals out and leisure activities"
    ),
    RESTAURANTS(
        DINING_ENTERTAINMENT,
        "Restaurants",
        "ic_restaurants",
        "Meals eaten at restaurants"
    ),
    BARS_ALCOHOL(
        DINING_ENTERTAINMENT,
        "Bars & Alcohol",
        "ic_bars_alcohol",
        "Spending on bars or alcoholic beverages"
    ),
    MOVIES_STREAMING(
        DINING_ENTERTAINMENT,
        "Movies & Streaming",
        "ic_movies_streaming",
        "Cinema tickets or streaming subscriptions"
    ),
    CONCERTS_EVENTS(
        DINING_ENTERTAINMENT,
        "Concerts & Events",
        "ic_concerts_events",
        "Tickets to concerts, shows, or events"
    ),
    VACATIONS(DINING_ENTERTAINMENT, "Vacations", "ic_vacations", "Travel and holiday expenses"),

    // 6. Health & Fitness
    HEALTH_FITNESS(
        EXPENSE,
        "Health & Fitness",
        "ic_category_health_fitness",
        "Medical and wellness costs"
    ),
    DOCTOR_DENTIST(
        HEALTH_FITNESS,
        "Doctor/Dentist",
        "ic_doctor_dentist",
        "Costs for medical or dental visits"
    ),
    PHARMACY(HEALTH_FITNESS, "Pharmacy", "ic_pharmacy", "Medication and health products"),
    HEALTH_INSURANCE_EXPENSE(
        HEALTH_FITNESS,
        "Health Insurance",
        "ic_health_insurance_expense",
        "Insurance premiums for health coverage"
    ),
    GYM_FITNESS(
        HEALTH_FITNESS,
        "Gym/Fitness",
        "ic_gym_fitness",
        "Gym memberships, fitness classes"
    ),

    // 7. Insurance & Financial
    INSURANCE_FINANCIAL(
        EXPENSE,
        "Insurance & Financial",
        "ic_category_insurance_financial",
        "Financial services and insurance expenses"
    ),
    LIFE_INSURANCE_EXPENSE(
        INSURANCE_FINANCIAL,
        "Life Insurance",
        "ic_life_insurance_expense",
        "Premiums for life insurance"
    ),
    BANK_FEES(INSURANCE_FINANCIAL, "Bank Fees", "ic_bank_fees", "Service charges from banks"),
    LOAN_PAYMENTS(
        INSURANCE_FINANCIAL,
        "Loan Payments",
        "ic_loan_payments",
        "Monthly loan or credit payments"
    ),
    TAXES_EXPENSE(
        INSURANCE_FINANCIAL,
        "Taxes",
        "ic_taxes_expense",
        "Non-property taxes or additional tax payments"
    ),

    // 8. Personal Care & Clothing
    PERSONAL_CARE_CLOTHING(
        EXPENSE,
        "Personal Care & Clothing",
        "ic_category_personal_care_clothing",
        "Personal grooming and apparel"
    ),
    HAIRCARE(PERSONAL_CARE_CLOTHING, "Haircare", "ic_haircare", "Haircuts, styling products"),
    SKINCARE(
        PERSONAL_CARE_CLOTHING,
        "Skincare",
        "ic_skincare",
        "Lotions, creams, skincare treatments"
    ),
    COSMETICS(PERSONAL_CARE_CLOTHING, "Cosmetics", "ic_cosmetics", "Make-up and related products"),
    CLOTHING_EXPENSE(
        PERSONAL_CARE_CLOTHING,
        "Clothing",
        "ic_clothing_expense",
        "Apparel and accessories"
    ),
    LAUNDRY(PERSONAL_CARE_CLOTHING, "Laundry", "ic_laundry", "Dry cleaning or laundry services"),

    // 9. Education & Childcare
    EDUCATION_CHILDCARE(
        EXPENSE,
        "Education & Childcare",
        "ic_category_education_childcare",
        "Costs for schooling and child-related care"
    ),
    TUITION(EDUCATION_CHILDCARE, "Tuition", "ic_tuition", "School or university fees"),
    BOOKS_SUPPLIES(
        EDUCATION_CHILDCARE,
        "Books & Supplies",
        "ic_books_supplies",
        "Textbooks and educational materials"
    ),
    CHILDCARE_EXPENSE(
        EDUCATION_CHILDCARE,
        "Childcare",
        "ic_childcare_expense",
        "Daycare or babysitting costs"
    ),
    TRAINING_CERTIFICATIONS(
        EDUCATION_CHILDCARE,
        "Training & Certifications",
        "ic_training_certifications",
        "Professional courses and certifications"
    ),

    // 10. Miscellaneous & Gifts
    MISC_GIFTS(
        EXPENSE,
        "Miscellaneous & Gifts",
        "ic_category_misc_gifts",
        "Other expenses and gifts"
    ),
    GIFTS_DONATIONS(
        MISC_GIFTS,
        "Gifts & Donations",
        "ic_gifts_donations",
        "Presents given, charitable donations"
    ),
    PET_EXPENSES(MISC_GIFTS, "Pet Expenses", "ic_pet_expenses", "Pet food, vet costs, etc."),
    OTHER_EXPENSES(
        MISC_GIFTS,
        "Other Expenses",
        "ic_other_expenses",
        "Expenses not fitting other categories"
    ),
    UNCATEGORIZED(
        MISC_GIFTS,
        "Uncategorized",
        "ic_uncategorized",
        "Not yet assigned to any category"
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
        // 获取所有顶级分类
        fun getAllParentCategories(): List<Category> {
            return entries.filter { it.parentCategory == null }
        }

        // 获取特定父类的所有子类
        fun getSubCategories(parentCategory: Category): List<Category> {
            return entries.filter { it.parentCategory == parentCategory }
        }

        // 根据名称获取分类
        fun fromCategoryName(categoryName: String): Category? {
            return entries.find { it.categoryName == categoryName }
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
         * return all level 1 categories
         */
        fun getLevel1Categories(): List<Category> {
            return getSubCategories(INCOME) + getSubCategories(EXPENSE)
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
                val subs = getSubCategories(parent).joinToString(", ") { it.name }  // 使用 name 代替 categoryName
                if (subs.isBlank()) parent.name else "${parent.name}: $subs"  // 使用 name 代替 categoryName
            }

            val expenseCategories = getSubCategories(EXPENSE).joinToString("\n") { parent ->
                val subs = getSubCategories(parent).joinToString(", ") { it.name }  // 使用 name 代替 categoryName
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
If you cannot find a suitable category from the user's description, category can be null.
If information is missing or not provided in the user's input, set that field to null.
If can't indentify the transaction date, use the user's local date as the transaction date.

Return only the JSON object and nothing else.
[Example]
input: 
text:"I spent $50 on groceries on 2024-11-15"
userLocalDate: 2024-11-20

output:
{
    "amount": 50,
    "remark": "on groceries",
    "category": "GROCERIES",
    "transactionType": "expense",
    "transactionDate": "2024-11-15",
}

INPUT:
text:"It cost me $100 to fix my car yesterday"
userLocalDate: 2024-12-20

OUTPUT:
{
    "amount": 100,
    "remark": "to fix my car",
    "category": "AUTO_MAINTENANCE_EXPENSE",
    "transactionType": "expense",
    "transactionDate": "2024-12-19",
}

INPUT:
text:"I received $500 as a gift"
userLocalDate: 2024-12-25
OUTPUT:
{
    "amount": 500,
    "remark": "as a gift",
    "category": "Gifts Received",
    "transactionType": "income",
    "transactionDate": "2024-12-25",
}
         """.trimIndent()
        }
    }


    override fun toString(): String {
        return super.toString()
    }
}



