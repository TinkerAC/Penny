package app.penny.core.domain.enum

import androidx.compose.runtime.TestOnly


enum class Category(
    val parentCategory: Category?,
    val categoryName: String,
    val categoryIcon: String,
    val description: String
) {
    // 顶级分类
    INCOME(null, "Income", "ic_category_income", "All types of income"),
    EXPENSE(null, "Expense", "ic_category_expense", "All types of expenses"),

    // 支出一级分类 (约10个)
    EXPENSE_HOUSING(EXPENSE, "Housing", "ic_category_housing", "Housing related expenses"),
    EXPENSE_TRANSPORTATION(
        EXPENSE,
        "Transportation",
        "ic_category_transportation",
        "Transportation related expenses"
    ),
    EXPENSE_FOOD(EXPENSE, "Food", "ic_category_food", "Food and dining expenses"),
    EXPENSE_HEALTH(EXPENSE, "Health", "ic_category_health", "Health and medical expenses"),
    EXPENSE_PERSONAL_CARE(
        EXPENSE,
        "Personal Care",
        "ic_category_personal_care",
        "Personal care and grooming"
    ),
    EXPENSE_ENTERTAINMENT(
        EXPENSE,
        "Entertainment",
        "ic_category_entertainment",
        "Movies, music, games, and more"
    ),
    EXPENSE_EDUCATION(
        EXPENSE,
        "Education",
        "ic_category_education",
        "Tuition and educational expenses"
    ),
    EXPENSE_INSURANCE(EXPENSE, "Insurance", "ic_category_insurance", "Various insurance payments"),
    EXPENSE_FINANCIAL(
        EXPENSE,
        "Financial",
        "ic_category_financial",
        "Loan, credit, and investment expenses"
    ),
    EXPENSE_MISCELLANEOUS(
        EXPENSE,
        "Miscellaneous",
        "ic_category_miscellaneous",
        "Other general expenses"
    ),

    // 支出二级分类 - Housing
    EXPENSE_HOUSING_MORTGAGE(
        EXPENSE_HOUSING,
        "Mortgage or Rent",
        "ic_category_housing_mortgage",
        "Home mortgage or rent payments"
    ),
    EXPENSE_HOUSING_UTILITIES(
        EXPENSE_HOUSING,
        "Utilities",
        "ic_category_housing_utilities",
        "Electricity, water, gas, etc."
    ),
    EXPENSE_HOUSING_MAINTENANCE(
        EXPENSE_HOUSING,
        "Maintenance",
        "ic_category_housing_maintenance",
        "Home repairs and maintenance"
    ),
    EXPENSE_HOUSING_PROPERTY_TAX(
        EXPENSE_HOUSING,
        "Property Tax",
        "ic_category_housing_property_tax",
        "Annual property tax payments"
    ),
    EXPENSE_HOUSING_OTHER(
        EXPENSE_HOUSING,
        "Other",
        "ic_category_housing_other",
        "Other housing expenses"
    ),

    // 支出二级分类 - Transportation
    EXPENSE_TRANSPORTATION_FUEL(
        EXPENSE_TRANSPORTATION,
        "Fuel",
        "ic_category_transportation_fuel",
        "Gasoline or diesel for vehicles"
    ),
    EXPENSE_TRANSPORTATION_PUBLIC_TRANSPORT(
        EXPENSE_TRANSPORTATION,
        "Public Transport",
        "ic_category_transportation_public_transport",
        "Bus, train, subway fares"
    ),
    EXPENSE_TRANSPORTATION_PARKING(
        EXPENSE_TRANSPORTATION,
        "Parking",
        "ic_category_transportation_parking",
        "Parking fees and tolls"
    ),
    EXPENSE_TRANSPORTATION_VEHICLE_MAINTENANCE(
        EXPENSE_TRANSPORTATION,
        "Vehicle Maintenance",
        "ic_category_transportation_vehicle_maintenance",
        "Car repairs and servicing"
    ),
    EXPENSE_TRANSPORTATION_OTHER(
        EXPENSE_TRANSPORTATION,
        "Other",
        "ic_category_transportation_other",
        "Other transportation expenses"
    ),

    // 支出二级分类 - Food
    EXPENSE_FOOD_GROCERIES(
        EXPENSE_FOOD,
        "Groceries",
        "ic_category_food_groceries",
        "Household food purchases"
    ),
    EXPENSE_FOOD_DINING_OUT(
        EXPENSE_FOOD,
        "Dining Out",
        "ic_category_food_dining_out",
        "Restaurant and café expenses"
    ),
    EXPENSE_FOOD_SNACKS_COFFEE(
        EXPENSE_FOOD,
        "Snacks & Coffee",
        "ic_category_food_snacks_coffee",
        "Quick bites and coffee runs"
    ),
    EXPENSE_FOOD_DELIVERY(
        EXPENSE_FOOD,
        "Delivery",
        "ic_category_food_delivery",
        "Food delivery services"
    ),
    EXPENSE_FOOD_OTHER(
        EXPENSE_FOOD,
        "Other",
        "ic_category_food_other",
        "Other food-related expenses"
    ),

    // 支出二级分类 - Health
    EXPENSE_HEALTH_INSURANCE(
        EXPENSE_HEALTH,
        "Health Insurance",
        "ic_category_health_insurance",
        "Health insurance premiums"
    ),
    EXPENSE_HEALTH_MEDICAL_BILLS(
        EXPENSE_HEALTH,
        "Medical Bills",
        "ic_category_health_medical_bills",
        "Doctor visits, hospital bills"
    ),
    EXPENSE_HEALTH_PHARMACY(
        EXPENSE_HEALTH,
        "Pharmacy",
        "ic_category_health_pharmacy",
        "Medications and supplements"
    ),
    EXPENSE_HEALTH_DENTAL_CARE(
        EXPENSE_HEALTH,
        "Dental Care",
        "ic_category_health_dental_care",
        "Dental check-ups and procedures"
    ),
    EXPENSE_HEALTH_OTHER(
        EXPENSE_HEALTH,
        "Other",
        "ic_category_health_other",
        "Other health expenses"
    ),

    // 支出二级分类 - Personal Care
    EXPENSE_PERSONAL_CARE_CLOTHING(
        EXPENSE_PERSONAL_CARE,
        "Clothing",
        "ic_category_personal_care_clothing",
        "Apparel and footwear"
    ),
    EXPENSE_PERSONAL_CARE_HAIR_BEAUTY(
        EXPENSE_PERSONAL_CARE,
        "Hair & Beauty",
        "ic_category_personal_care_hair_beauty",
        "Salon, spa treatments"
    ),
    EXPENSE_PERSONAL_CARE_FITNESS(
        EXPENSE_PERSONAL_CARE,
        "Fitness",
        "ic_category_personal_care_fitness",
        "Gym memberships, fitness classes"
    ),
    EXPENSE_PERSONAL_CARE_COSMETICS(
        EXPENSE_PERSONAL_CARE,
        "Cosmetics",
        "ic_category_personal_care_cosmetics",
        "Makeup, skincare products"
    ),
    EXPENSE_PERSONAL_CARE_OTHER(
        EXPENSE_PERSONAL_CARE,
        "Other",
        "ic_category_personal_care_other",
        "Other personal care expenses"
    ),

    // 支出二级分类 - Entertainment
    EXPENSE_ENTERTAINMENT_MOVIES_SHOWS(
        EXPENSE_ENTERTAINMENT,
        "Movies & Shows",
        "ic_category_entertainment_movies_shows",
        "Cinema tickets, theater shows"
    ),
    EXPENSE_ENTERTAINMENT_MUSIC_CONCERTS(
        EXPENSE_ENTERTAINMENT,
        "Music & Concerts",
        "ic_category_entertainment_music_concerts",
        "Concert tickets, music events"
    ),
    EXPENSE_ENTERTAINMENT_GAMES_APPS(
        EXPENSE_ENTERTAINMENT,
        "Games & Apps",
        "ic_category_entertainment_games_apps",
        "Video games, mobile apps"
    ),
    EXPENSE_ENTERTAINMENT_STREAMING(
        EXPENSE_ENTERTAINMENT,
        "Streaming Services",
        "ic_category_entertainment_streaming",
        "Monthly fees for Netflix, etc."
    ),
    EXPENSE_ENTERTAINMENT_OTHER(
        EXPENSE_ENTERTAINMENT,
        "Other",
        "ic_category_entertainment_other",
        "Other entertainment expenses"
    ),

    // 支出二级分类 - Education
    EXPENSE_EDUCATION_TUITION(
        EXPENSE_EDUCATION,
        "Tuition",
        "ic_category_education_tuition",
        "School and university fees"
    ),
    EXPENSE_EDUCATION_BOOKS_SUPPLIES(
        EXPENSE_EDUCATION,
        "Books & Supplies",
        "ic_category_education_books_supplies",
        "Textbooks, stationery"
    ),
    EXPENSE_EDUCATION_ONLINE_COURSES(
        EXPENSE_EDUCATION,
        "Online Courses",
        "ic_category_education_online_courses",
        "E-learning and courses"
    ),
    EXPENSE_EDUCATION_STUDENT_LOANS(
        EXPENSE_EDUCATION,
        "Student Loans",
        "ic_category_education_student_loans",
        "Student loan repayments"
    ),
    EXPENSE_EDUCATION_OTHER(
        EXPENSE_EDUCATION,
        "Other",
        "ic_category_education_other",
        "Other educational expenses"
    ),

    // 支出二级分类 - Insurance
    EXPENSE_INSURANCE_AUTO_INSURANCE(
        EXPENSE_INSURANCE,
        "Auto Insurance",
        "ic_category_insurance_auto",
        "Car insurance premiums"
    ),
    EXPENSE_INSURANCE_HEALTH_INSURANCE(
        EXPENSE_INSURANCE,
        "Health Insurance",
        "ic_category_insurance_health",
        "Health insurance premiums"
    ),
    EXPENSE_INSURANCE_HOME_INSURANCE(
        EXPENSE_INSURANCE,
        "Home Insurance",
        "ic_category_insurance_home",
        "Homeowner’s insurance"
    ),
    EXPENSE_INSURANCE_LIFE_INSURANCE(
        EXPENSE_INSURANCE,
        "Life Insurance",
        "ic_category_insurance_life",
        "Life insurance premiums"
    ),
    EXPENSE_INSURANCE_OTHER(
        EXPENSE_INSURANCE,
        "Other",
        "ic_category_insurance_other",
        "Other insurance expenses"
    ),

    // 支出二级分类 - Financial
    EXPENSE_FINANCIAL_LOAN_PAYMENTS(
        EXPENSE_FINANCIAL,
        "Loan Payments",
        "ic_category_financial_loan_payments",
        "Mortgage, personal loan repayments"
    ),
    EXPENSE_FINANCIAL_CREDIT_CARD_PAYMENTS(
        EXPENSE_FINANCIAL,
        "Credit Card Payments",
        "ic_category_financial_credit_card",
        "Credit card bill payments"
    ),
    EXPENSE_FINANCIAL_INVESTMENTS(
        EXPENSE_FINANCIAL,
        "Investments",
        "ic_category_financial_investments",
        "Stocks, bonds, and other investments"
    ),
    EXPENSE_FINANCIAL_BANKING_FEES(
        EXPENSE_FINANCIAL,
        "Banking Fees",
        "ic_category_financial_banking_fees",
        "Bank account and ATM fees"
    ),
    EXPENSE_FINANCIAL_OTHER(
        EXPENSE_FINANCIAL,
        "Other",
        "ic_category_financial_other",
        "Other financial expenses"
    ),

    // 支出二级分类 - Miscellaneous
    EXPENSE_MISCELLANEOUS_GIFTS(
        EXPENSE_MISCELLANEOUS,
        "Gifts",
        "ic_category_miscellaneous_gifts",
        "Presents for others"
    ),
    EXPENSE_MISCELLANEOUS_DONATIONS(
        EXPENSE_MISCELLANEOUS,
        "Donations",
        "ic_category_miscellaneous_donations",
        "Charitable contributions"
    ),
    EXPENSE_MISCELLANEOUS_SUBSCRIPTIONS(
        EXPENSE_MISCELLANEOUS,
        "Subscriptions",
        "ic_category_miscellaneous_subscriptions",
        "Monthly or yearly service fees"
    ),
    EXPENSE_MISCELLANEOUS_PET_CARE(
        EXPENSE_MISCELLANEOUS,
        "Pet Care",
        "ic_category_miscellaneous_pet_care",
        "Pet food, vet bills"
    ),
    EXPENSE_MISCELLANEOUS_OTHER(
        EXPENSE_MISCELLANEOUS,
        "Other",
        "ic_category_miscellaneous_other",
        "Other miscellaneous expenses"
    ),


    // 收入一级分类 (约5个)
    INCOME_SALARY(INCOME, "Salary", "ic_category_salary", "Regular salary and wages"),
    INCOME_BUSINESS(
        INCOME,
        "Business",
        "ic_category_business",
        "Business or self-employment income"
    ),
    INCOME_INVESTMENT(INCOME, "Investment", "ic_category_investment", "Income from investments"),
    INCOME_FREELANCE(INCOME, "Freelance", "ic_category_freelance", "Income from freelance work"),
    INCOME_MISCELLANEOUS(
        INCOME,
        "Miscellaneous",
        "ic_category_income_miscellaneous",
        "Other sources of income"
    ),

    // 收入二级分类 - Salary
    INCOME_SALARY_BASIC_SALARY(
        INCOME_SALARY,
        "Basic Salary",
        "ic_category_salary_basic",
        "Base salary income"
    ),
    INCOME_SALARY_BONUS(
        INCOME_SALARY,
        "Bonus",
        "ic_category_salary_bonus",
        "Performance-based bonuses"
    ),
    INCOME_SALARY_OVERTIME(
        INCOME_SALARY,
        "Overtime",
        "ic_category_salary_overtime",
        "Overtime pay"
    ),
    INCOME_SALARY_COMMISSIONS(
        INCOME_SALARY,
        "Commissions",
        "ic_category_salary_commissions",
        "Sales or performance commissions"
    ),
    INCOME_SALARY_OTHER(
        INCOME_SALARY,
        "Other",
        "ic_category_salary_other",
        "Other salary-related income"
    ),

    // 收入二级分类 - Business
    INCOME_BUSINESS_SALES(
        INCOME_BUSINESS,
        "Sales",
        "ic_category_business_sales",
        "Income from product sales"
    ),
    INCOME_BUSINESS_SERVICES(
        INCOME_BUSINESS,
        "Services",
        "ic_category_business_services",
        "Income from provided services"
    ),
    INCOME_BUSINESS_ROYALTIES(
        INCOME_BUSINESS,
        "Royalties",
        "ic_category_business_royalties",
        "Royalty income"
    ),
    INCOME_BUSINESS_OTHER(
        INCOME_BUSINESS,
        "Other",
        "ic_category_business_other",
        "Other business income"
    ),

    // 收入二级分类 - Investment
    INCOME_INVESTMENT_DIVIDENDS(
        INCOME_INVESTMENT,
        "Dividends",
        "ic_category_investment_dividends",
        "Dividend income from stocks"
    ),
    INCOME_INVESTMENT_INTEREST(
        INCOME_INVESTMENT,
        "Interest",
        "ic_category_investment_interest",
        "Interest from savings or bonds"
    ),
    INCOME_INVESTMENT_CAPITAL_GAINS(
        INCOME_INVESTMENT,
        "Capital Gains",
        "ic_category_investment_capital_gains",
        "Profit from selling investments"
    ),
    INCOME_INVESTMENT_RENTAL_INCOME(
        INCOME_INVESTMENT,
        "Rental Income",
        "ic_category_investment_rental_income",
        "Income from renting properties"
    ),
    INCOME_INVESTMENT_OTHER(
        INCOME_INVESTMENT,
        "Other",
        "ic_category_investment_other",
        "Other investment income"
    ),

    // 收入二级分类 - Freelance
    INCOME_FREELANCE_PROJECTS(
        INCOME_FREELANCE,
        "Projects",
        "ic_category_freelance_projects",
        "Project-based freelance work"
    ),
    INCOME_FREELANCE_CONSULTING(
        INCOME_FREELANCE,
        "Consulting",
        "ic_category_freelance_consulting",
        "Consulting or advisory services"
    ),
    INCOME_FREELANCE_TIPS(
        INCOME_FREELANCE,
        "Tips",
        "ic_category_freelance_tips",
        "Gratuities from clients"
    ),
    INCOME_FREELANCE_OTHER(
        INCOME_FREELANCE,
        "Other",
        "ic_category_freelance_other",
        "Other freelance income"
    ),

    // 收入二级分类 - Miscellaneous
    INCOME_MISCELLANEOUS_GIFTS(
        INCOME_MISCELLANEOUS,
        "Gifts",
        "ic_category_income_miscellaneous_gifts",
        "Monetary gifts received"
    ),
    INCOME_MISCELLANEOUS_GOVERNMENT_BENEFITS(
        INCOME_MISCELLANEOUS,
        "Government Benefits",
        "ic_category_income_miscellaneous_benefits",
        "Social security, unemployment benefits"
    ),
    INCOME_MISCELLANEOUS_LOTTERY_WINNINGS(
        INCOME_MISCELLANEOUS,
        "Lottery Winnings",
        "ic_category_income_miscellaneous_lottery",
        "Income from lotteries or gambling"
    ),
    INCOME_MISCELLANEOUS_OTHER(
        INCOME_MISCELLANEOUS,
        "Other",
        "ic_category_income_miscellaneous_other",
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
            return getAllParentCategories()
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
    "transactionType": "expense",
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
    "transactionType": "expense",
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
    "transactionType": "income",
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



