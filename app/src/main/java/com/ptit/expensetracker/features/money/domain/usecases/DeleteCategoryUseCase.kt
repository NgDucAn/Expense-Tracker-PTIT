package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) : UseCase<Unit, DeleteCategoryUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Unit> {
        return try {
            val result = categoryRepository.deleteCategory(params.id)
            if (result.isSuccess) {
                Either.Right(Unit)
            } else {
                Either.Left(Failure.DatabaseError)
            }
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val id: String)
}