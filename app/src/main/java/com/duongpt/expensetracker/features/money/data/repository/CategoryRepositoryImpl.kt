package com.duongpt.expensetracker.features.money.data.repository

import com.duongpt.expensetracker.features.money.data.data_source.local.dao.CategoryDao
import com.duongpt.expensetracker.features.money.data.data_source.local.model.CategoryEntity
import com.duongpt.expensetracker.features.money.domain.model.Category
import com.duongpt.expensetracker.features.money.domain.model.CategoryGroup
import com.duongpt.expensetracker.features.money.domain.model.CategoryType
import com.duongpt.expensetracker.features.money.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        val typeInt = when (type) {
            CategoryType.INCOME -> 1
            CategoryType.EXPENSE -> 2
            CategoryType.DEBT_LOAN -> 3
            CategoryType.UNKNOWN -> -1
        }
        return categoryDao.getCategoriesByType(typeInt).map { entities ->
            entities.map { it.toCategory() }
        }
    }

    override suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getCategoryById(id)?.toCategory()
    }

    override suspend fun insertCategories(categories: List<Category>): Result<Unit> {
        return try {
            categoryDao.insertCategories(categories.map { toCategoryEntity(it) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertCategory(category: Category): Result<Unit> {
        return try {
            categoryDao.insertCategory(toCategoryEntity(category))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            categoryDao.deleteCategory(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkCategoriesExist(): Boolean {
        // Check if any categories exist in the database
        return categoryDao.getAllCategories().map { it.isNotEmpty() }.first()
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.findCategoryByName(name)?.toCategory()
    }

    override fun getCategoryGroupsByType(type: CategoryType): Flow<List<CategoryGroup>> {
        return getCategoriesByType(type).map { categories ->
            groupCategoriesToCategoryGroups(categories)
        }
    }

    override fun getAllCategoryGroups(): Flow<Map<CategoryType, List<CategoryGroup>>> {
        return getAllCategories().map { allCategories ->
            val groupedByType = allCategories.groupBy { it.type }
            groupedByType.mapValues { (_, categories) ->
                groupCategoriesToCategoryGroups(categories)
            }
        }
    }

    // Helper function to convert domain model to entity
    private fun toCategoryEntity(category: Category): CategoryEntity {
        val typeInt = when (category.type) {
            CategoryType.INCOME -> 1
            CategoryType.EXPENSE -> 2
            CategoryType.DEBT_LOAN -> 3
            CategoryType.UNKNOWN -> -1
        }

        return CategoryEntity(
            id = category.id,
            title = category.title,
            icon = category.icon,
            type = typeInt,
            parentName = category.parentName,
            metaData = category.metaData
        )
    }

    // Helper function to group categories to category groups
    private fun groupCategoriesToCategoryGroups(categories: List<Category>): List<CategoryGroup> {
        // Group categories by their parent name
        val groupedByParent = categories.groupBy { it.parentName }

        // Create a list to store category groups
        val result = mutableListOf<CategoryGroup>()

        // For each top-level category or distinct parent name, create a CategoryGroup
        val processedParentNames = mutableSetOf<String>()

        // Process all categories to find unique parent names
        categories.forEach { category ->
            val parentName = category.parentName ?: category.title

            if (!processedParentNames.contains(parentName)) {
                processedParentNames.add(parentName)

                // Find the parent category to get its title and icon
                val parentCategory = if (category.parentName == null) {
                    category // This is a top-level category
                } else {
                    // Find a category with this parent name
                    categories.find { it.title == category.parentName } ?: category
                }

                // Get all subcategories for this parent
                val subcategories = if (category.parentName == null) {
                    // For top-level categories without subcategories, they are their own group
                    listOf(category)
                } else {
                    // Get all categories with this parent name
                    categories.filter { it.parentName == parentName }
                }

                // Create a CategoryGroup
                val categoryGroup = CategoryGroup(
                    parentName = parentName,
                    parentTitleResName = parentCategory.title,
                    parentIconResName = parentCategory.icon,
                    type = parentCategory.type,
                    subCategories = subcategories
                )

                result.add(categoryGroup)
            }
        }

        return result
    }
}