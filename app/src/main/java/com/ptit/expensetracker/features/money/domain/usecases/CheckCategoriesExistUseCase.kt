package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.repository.CategoryRepository
import javax.inject.Inject

class CheckCategoriesExistUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) : UseCase<Boolean, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Boolean> {
        return try {
            val exist = categoryRepository.checkCategoriesExist()
            Either.Right(exist)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
}