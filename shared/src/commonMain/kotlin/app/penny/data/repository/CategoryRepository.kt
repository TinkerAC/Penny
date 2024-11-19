package app.penny.data.repository

import app.penny.domain.model.CategoryModel

interface CategoryRepository {
    suspend fun insertCategory(categoryModel: CategoryModel)
    suspend fun getCategoryById(categoryId: Long): CategoryModel
    suspend fun getAllCategories(): List<CategoryModel>
    suspend fun updateCategory(categoryModel: CategoryModel)
    suspend fun deleteCategory(categoryId: Long)

}