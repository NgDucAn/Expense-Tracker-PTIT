package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.CategoryGroup
import com.duongpt.expensetracker.features.money.domain.model.CategoryType
import com.duongpt.expensetracker.features.money.domain.repository.CategoryRepository
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