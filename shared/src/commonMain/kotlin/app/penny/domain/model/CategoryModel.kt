package app.penny.domain.model

data class CategoryModel(
    val id: Long = 0,
    val parentId: Long? = null,
    val name: String = "",
    val iconUri: String = "",
)