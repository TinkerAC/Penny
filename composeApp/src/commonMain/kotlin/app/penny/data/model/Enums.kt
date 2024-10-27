package app.penny.data.model

class TransactionType {
    companion object {
        fun valueOf(transactionType: String): String {
            if (transactionType == EXPENSE) {
                return EXPENSE
            } else if (transactionType == INCOME) {
                return INCOME
            } else if (transactionType == TRANSFER) {
                return TRANSFER
            }
            return ""
        }

        const val EXPENSE = "expense"
        const val INCOME = "income"
        const val TRANSFER = "transfer"
    }
}