package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.Category
import com.duongpt.expensetracker.features.money.domain.repository.CategoryRepository
import javax.inject.Inject

class InsertCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) : UseCase<Unit, InsertCategoryUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Unit> {
        return try {
            val result = categoryRepository.insertCategory(params.category)
            if (result.isSuccess) {
                Either.Right(Unit)
            } else {
                Either.Left(Failure.DatabaseError)
            }
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val category: Category)
}