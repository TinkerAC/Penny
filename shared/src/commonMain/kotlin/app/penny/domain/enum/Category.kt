package app.penny.domain.enum

enum class Category(
    val parentCategory: Category?,
    val categoryName: String,
    val categoryIcon: String,
) {
    // 顶级类别
    INCOME(null, "Income", "ic_category_income"),
    EXPENSE(null, "Expense", "ic_category_expense"),

    // 收入子类别
    SALARY(INCOME, "Salary", "ic_salary"),
    BASIC_SALARY(SALARY, "Base Salary", "ic_base_salary"),
    BONUS(SALARY, "Bonus", "ic_bonus"),
    OVERTIME(SALARY, "Overtime Pay", "ic_overtime"),
    COMMISSION(SALARY, "Commission", "ic_commission"),

    BUSINESS(INCOME, "Business Income", "ic_business"),
    SALES(BUSINESS, "Sales", "ic_sales"),
    SERVICE(BUSINESS, "Service Revenue", "ic_service"),
    CONSULTING(BUSINESS, "Consulting Fees", "ic_consulting"),
    ROYALTIES(BUSINESS, "Royalties", "ic_royalties"),

    INVESTMENT(INCOME, "Investment Income", "ic_investment"),
    DIVIDENDS(INVESTMENT, "Dividends", "ic_dividends"),
    INTEREST(INVESTMENT, "Interest", "ic_interest"),
    CAPITAL_GAINS(INVESTMENT, "Capital Gains", "ic_capital_gains"),
    RENTAL_INCOME(INVESTMENT, "Rental Income", "ic_rental"),

    RETIREMENT(INCOME, "Retirement Income", "ic_retirement"),
    PENSION(RETIREMENT, "Pension", "ic_pension"),
    SOCIAL_SECURITY(RETIREMENT, "Social Security", "ic_social_security"),
    ANNUITY(RETIREMENT, "Annuity", "ic_annuity"),
    IRA_DISTRIBUTION(RETIREMENT, "IRA Distribution", "ic_ira"),

    PART_TIME(INCOME, "Part-time Income", "ic_part_time"),
    FREELANCE(PART_TIME, "Freelance Work", "ic_freelance"),
    GIG(PART_TIME, "Gig Work", "ic_gig"),
    TUTORING(PART_TIME, "Tutoring", "ic_tutoring"),
    BABYSITTING(PART_TIME, "Babysitting", "ic_babysitting"),

    GOVERNMENT(INCOME, "Government Benefits", "ic_government"),
    WELFARE(GOVERNMENT, "Welfare", "ic_welfare"),
    UNEMPLOYMENT(GOVERNMENT, "Unemployment", "ic_unemployment"),
    DISABILITY(GOVERNMENT, "Disability", "ic_disability"),
    VETERANS_BENEFITS(GOVERNMENT, "Veterans Benefits", "ic_veterans"),

    GIFTS(INCOME, "Gifts Received", "ic_gifts"),
    CASH_GIFTS(GIFTS, "Cash Gifts", "ic_cash_gifts"),
    INHERITANCE(GIFTS, "Inheritance", "ic_inheritance"),
    TRUST_FUND(GIFTS, "Trust Fund", "ic_trust"),
    LOTTERY(GIFTS, "Lottery Winnings", "ic_lottery"),

    TAX_REFUND(INCOME, "Tax Refund", "ic_tax_refund"),
    FEDERAL_REFUND(TAX_REFUND, "Federal Refund", "ic_federal_refund"),
    STATE_REFUND(TAX_REFUND, "State Refund", "ic_state_refund"),
    LOCAL_REFUND(TAX_REFUND, "Local Refund", "ic_local_refund"),

    INSURANCE_PAYOUT(INCOME, "Insurance Payout", "ic_insurance"),
    HEALTH_INSURANCE_PAYOUT(INSURANCE_PAYOUT, "Health Insurance", "ic_health_insurance"),
    CAR_INSURANCE_PAYOUT(INSURANCE_PAYOUT, "Car Insurance", "ic_car_insurance"),
    LIFE_INSURANCE_PAYOUT(INSURANCE_PAYOUT, "Life Insurance", "ic_life_insurance"),
    PROPERTY_INSURANCE_PAYOUT(INSURANCE_PAYOUT, "Property Insurance", "ic_property_insurance"),

    OTHER_INCOME(INCOME, "Other Income", "ic_other_income"),
    ALIMONY(OTHER_INCOME, "Alimony", "ic_alimony"),
    CHILD_SUPPORT(OTHER_INCOME, "Child Support", "ic_child_support"),
    LEGAL_SETTLEMENT(OTHER_INCOME, "Legal Settlement", "ic_legal"),
    MISCELLANEOUS_INCOME(OTHER_INCOME, "Miscellaneous", "ic_misc"),

    // 支出子类别
    // Housing Categories
    HOUSING(EXPENSE, "Housing", "ic_category_housing"),
    RENT_OR_MORTGAGE(HOUSING, "Rent/Mortgage", "ic_category_rent_or_mortgage"),
    PROPERTY_TAXES(HOUSING, "Property Taxes", "ic_category_property_taxes"),
    HOME_INSURANCE(HOUSING, "Home Insurance", "ic_category_home_insurance"),
    HOME_MAINTENANCE(HOUSING, "Home Maintenance", "ic_category_home_maintenance"),
    HOUSE_CLEANING(HOUSING, "House Cleaning", "ic_category_house_cleaning"),

    // Utilities Categories
    UTILITIES(EXPENSE, "Utilities", "ic_category_utilities"),
    ELECTRICITY(UTILITIES, "Electricity", "ic_category_electricity"),
    WATER(UTILITIES, "Water", "ic_category_water"),
    NATURAL_GAS(UTILITIES, "Natural Gas", "ic_category_natural_gas"),
    INTERNET(UTILITIES, "Internet", "ic_category_internet"),
    PHONE_SERVICE(UTILITIES, "Phone Service", "ic_category_phone_service"),
    CABLE_TV(UTILITIES, "Cable TV", "ic_category_cable_tv"),

    // Transportation Categories
    TRANSPORTATION(EXPENSE, "Transportation", "ic_category_transportation"),
    FUEL(TRANSPORTATION, "Fuel", "ic_category_fuel"),
    AUTO_PAYMENT(TRANSPORTATION, "Auto Payment", "ic_category_auto_payment"),
    AUTO_INSURANCE(TRANSPORTATION, "Auto Insurance", "ic_category_auto_insurance"),
    PUBLIC_TRANSIT(TRANSPORTATION, "Public Transit", "ic_category_public_transit"),
    PARKING(TRANSPORTATION, "Parking", "ic_category_parking"),
    AUTO_MAINTENANCE(TRANSPORTATION, "Auto Maintenance", "ic_category_auto_maintenance"),
    RIDE_SHARING(TRANSPORTATION, "Ride Sharing", "ic_category_ride_sharing"),
    AIR_TRAVEL(TRANSPORTATION, "Air Travel", "ic_category_air_travel"),

    // Food & Dining Categories
    FOOD_AND_DINING(EXPENSE, "Food & Dining", "ic_category_food_and_dining"),
    GROCERIES(FOOD_AND_DINING, "Groceries", "ic_category_groceries"),
    RESTAURANTS(FOOD_AND_DINING, "Restaurants", "ic_category_restaurants"),
    FAST_FOOD(FOOD_AND_DINING, "Fast Food", "ic_category_fast_food"),
    COFFEE_SHOPS(FOOD_AND_DINING, "Coffee Shops", "ic_category_coffee_shops"),
    BARS_AND_ALCOHOL(FOOD_AND_DINING, "Bars & Alcohol", "ic_category_bars_and_alcohol"),
    SNACKS_AND_CANDY(FOOD_AND_DINING, "Snacks & Candy", "ic_category_snacks_and_candy"),

    // Entertainment Categories
    ENTERTAINMENT(EXPENSE, "Entertainment", "ic_category_entertainment"),
    MOVIES_AND_THEATER(ENTERTAINMENT, "Movies & Theater", "ic_category_movies_and_theater"),
    MUSIC_AND_CONCERTS(ENTERTAINMENT, "Music & Concerts", "ic_category_music_and_concerts"),
    GAMES_AND_HOBBIES(ENTERTAINMENT, "Games & Hobbies", "ic_category_games_and_hobbies"),
    AMUSEMENT_PARKS(ENTERTAINMENT, "Amusement Parks", "ic_category_amusement_parks"),
    VACATIONS(ENTERTAINMENT, "Vacations", "ic_category_vacations"),
    SUBSCRIPTIONS(ENTERTAINMENT, "Subscriptions", "ic_category_subscriptions"),
    NIGHTLIFE(ENTERTAINMENT, "Nightlife", "ic_category_nightlife"),

    // Health & Fitness Categories
    HEALTH_AND_FITNESS(EXPENSE, "Health & Fitness", "ic_category_health_and_fitness"),
    DOCTOR(HEALTH_AND_FITNESS, "Doctor", "ic_category_doctor"),
    DENTIST(HEALTH_AND_FITNESS, "Dentist", "ic_category_dentist"),
    PHARMACY(HEALTH_AND_FITNESS, "Pharmacy", "ic_category_pharmacy"),
    HEALTH_INSURANCE(HEALTH_AND_FITNESS, "Health Insurance", "ic_category_health_insurance"),
    GYM_AND_FITNESS(HEALTH_AND_FITNESS, "Gym & Fitness", "ic_category_gym_and_fitness"),
    SPA_AND_MASSAGE(HEALTH_AND_FITNESS, "Spa & Massage", "ic_category_spa_and_massage"),
    SPORTS_EQUIPMENT(HEALTH_AND_FITNESS, "Sports Equipment", "ic_category_sports_equipment"),

    // Insurance Categories
    INSURANCE(EXPENSE, "Insurance", "ic_category_insurance"),
    LIFE_INSURANCE(INSURANCE, "Life Insurance", "ic_category_life_insurance"),
    HOME_INSURANCE_DUPLICATE(INSURANCE, "Home Insurance", "ic_category_home_insurance"),
    AUTO_INSURANCE_DUPLICATE(INSURANCE, "Auto Insurance", "ic_category_auto_insurance"),
    HEALTH_INSURANCE_DUPLICATE(INSURANCE, "Health Insurance", "ic_category_health_insurance"),

    // Financial Categories
    FINANCIAL(EXPENSE, "Financial", "ic_category_financial"),
    BANK_FEES(FINANCIAL, "Bank Fees", "ic_category_bank_fees"),
    INTEREST_CHARGES(FINANCIAL, "Interest Charges", "ic_category_interest_charges"),
    INVESTMENT_FEES(FINANCIAL, "Investment Fees", "ic_category_investment_fees"),
    TAXES(FINANCIAL, "Taxes", "ic_category_taxes"),
    FINES_AND_PENALTIES(FINANCIAL, "Fines & Penalties", "ic_category_fines_and_penalties"),
    LOAN_PAYMENTS(FINANCIAL, "Loan Payments", "ic_category_loan_payments"),

    // Personal Care Categories
    PERSONAL_CARE(EXPENSE, "Personal Care", "ic_category_personal_care"),
    HAIRCARE(PERSONAL_CARE, "Haircare", "ic_category_haircare"),
    SKINCARE(PERSONAL_CARE, "Skincare", "ic_category_skincare"),
    COSMETICS(PERSONAL_CARE, "Cosmetics", "ic_category_cosmetics"),
    CLOTHING(PERSONAL_CARE, "Clothing", "ic_category_clothing"),
    LAUNDRY(PERSONAL_CARE, "Laundry", "ic_category_laundry"),

    // Education Categories
    EDUCATION(EXPENSE, "Education", "ic_category_education"),
    TUITION(EDUCATION, "Tuition", "ic_category_tuition"),
    BOOKS_AND_SUPPLIES(EDUCATION, "Books & Supplies", "ic_category_books_and_supplies"),
    STUDENT_LOAN_PAYMENTS(EDUCATION, "Student Loan Payments", "ic_category_student_loan_payments"),
    TRAINING_AND_CERTIFICATIONS(
        EDUCATION,
        "Training & Certifications",
        "ic_category_training_and_certifications"
    ),

    // Gifts & Donations Categories
    GIFTS_AND_DONATIONS(EXPENSE, "Gifts & Donations", "ic_category_gifts_and_donations"),
    GIFTS_GIVEN(GIFTS_AND_DONATIONS, "Gifts Given", "ic_category_gifts_given"),
    CHARITABLE_DONATIONS(
        GIFTS_AND_DONATIONS,
        "Charitable Donations",
        "ic_category_charitable_donations"
    ),
    TIPS_AND_GRATUITIES(
        GIFTS_AND_DONATIONS,
        "Tips & Gratuities",
        "ic_category_tips_and_gratuities"
    ),

    // Family Categories
    FAMILY(EXPENSE, "Family", "ic_category_family"),
    CHILDCARE(FAMILY, "Childcare", "ic_category_childcare"),
    ALLOWANCES(FAMILY, "Allowances", "ic_category_allowances"),
    ELDER_CARE(FAMILY, "Elder Care", "ic_category_elder_care"),

    // Pets Categories
    PETS(EXPENSE, "Pets", "ic_category_pets"),
    PET_FOOD(PETS, "Pet Food", "ic_category_pet_food"),
    VETERINARY(PETS, "Veterinary", "ic_category_veterinary"),
    PET_SUPPLIES(PETS, "Pet Supplies", "ic_category_pet_supplies"),
    PET_GROOMING(PETS, "Pet Grooming", "ic_category_pet_grooming"),

    // Shopping Categories
    SHOPPING(EXPENSE, "Shopping", "ic_category_shopping"),
    ELECTRONICS(SHOPPING, "Electronics", "ic_category_electronics"),
    HOME_IMPROVEMENT(SHOPPING, "Home Improvement", "ic_category_home_improvement"),
    APPLIANCES(SHOPPING, "Appliances", "ic_category_appliances"),
    FURNITURE(SHOPPING, "Furniture", "ic_category_furniture"),
    OFFICE_SUPPLIES(SHOPPING, "Office Supplies", "ic_category_office_supplies"),
    BABY_SUPPLIES(SHOPPING, "Baby Supplies", "ic_category_baby_supplies"),
    CLOTHING_AND_ACCESSORIES(
        SHOPPING,
        "Clothing & Accessories",
        "ic_category_clothing_and_accessories"
    ),
    BOOKS(SHOPPING, "Books", "ic_category_books"),
    MUSIC(SHOPPING, "Music", "ic_category_music"),


    // Miscellaneous Categories
    MISCELLANEOUS(EXPENSE, "Miscellaneous", "ic_category_miscellaneous"),
    OTHER_EXPENSES(MISCELLANEOUS, "Other Expenses", "ic_category_other_expenses"),
    UNCATEGORIZED(MISCELLANEOUS, "Uncategorized", "ic_category_uncategorized");


    // Add helper functions
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
        fun getCategoryByName(name: String): Category? {
            return entries.find { it.categoryName.equals(name, ignoreCase = true) }
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
    }


    override fun toString(): String {
        return super.toString()
    }
}
