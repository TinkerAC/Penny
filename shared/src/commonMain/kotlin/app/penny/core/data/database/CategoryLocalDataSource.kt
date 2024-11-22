package app.penny.core.data.database

import app.penny.database.CategoryEntity
import app.penny.database.CategoryQueries

class CategoryLocalDataSource(
    private val categoryQueries: CategoryQueries
) {

    fun insertCategory(categoryEntity: CategoryEntity) {
        categoryQueries.insertCategory(
            parent_id = categoryEntity.parent_id,
            name = categoryEntity.name,
            icon_uri = categoryEntity.icon_uri,
        )
    }

    fun getCategoryById(id: Long): CategoryEntity {
        return categoryQueries.getCategoryById(id).executeAsOne()
    }

    fun getAllCategories(): List<CategoryEntity> {
        return categoryQueries.getAllCategories().executeAsList()
    }


    fun updateCategory(categoryEntity: CategoryEntity) {
        categoryQueries.updateCategory(
            parent_id = categoryEntity.parent_id,
            name = categoryEntity.name,
            icon_uri = categoryEntity.icon_uri,
            id = categoryEntity.id
        )
    }


    fun deleteCategory(id: Long) {
        categoryQueries.deleteCategory(id)
    }


}