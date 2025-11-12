package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.Category
import com.ptit.expensetracker.features.money.domain.repository.CategoryRepository
import javax.inject.Inject

class InsertCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) : UseCase<Unit, InsertCategoriesUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Unit> {
        return try {
            val result = categoryRepository.insertCategories(params.categories)
            if (result.isSuccess) {
                Either.Right(Unit)
            } else {
                Either.Left(Failure.DatabaseError)
            }
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val categories: List<Category>)
}