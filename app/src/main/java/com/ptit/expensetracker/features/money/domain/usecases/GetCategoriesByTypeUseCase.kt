package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.CategoryGroup
import com.ptit.expensetracker.features.money.domain.model.CategoryType
import com.ptit.expensetracker.features.money.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCategoriesByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) : UseCase<List<CategoryGroup>, GetCategoriesByTypeUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<CategoryGroup>> {
        return try {
            val categories = categoryRepository.getCategoryGroupsByType(params.type).first()
            Either.Right(categories)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val type: CategoryType)
}