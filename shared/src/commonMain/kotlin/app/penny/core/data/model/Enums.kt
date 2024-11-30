package app.penny.core.data.model

enum class TransactionType {
    INCOME,
    EXPENSE,
//    TRANSFER
}


enum class MESSAGE_TYPE(
    val value: String
) {
    TEXT("text"),
    AUDIO("audio")
}
