package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.Category
import com.ptit.expensetracker.features.money.domain.repository.CategoryRepository
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