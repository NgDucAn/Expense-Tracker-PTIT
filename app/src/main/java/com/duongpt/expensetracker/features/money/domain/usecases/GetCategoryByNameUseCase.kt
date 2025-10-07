package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.Category
import com.duongpt.expensetracker.features.money.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case to find a category by name (title or metadata).
 * Performs case-insensitive search in the database.
 */
class GetCategoryByNameUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) : UseCase<Category, GetCategoryByNameUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Category> {
        return try {
            val category = categoryRepository.getCategoryByName(params.name)
            if (category != null) {
                Either.Right(category)
            } else {
                Either.Left(Failure.NotFound)
            }
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val name: String)
} 