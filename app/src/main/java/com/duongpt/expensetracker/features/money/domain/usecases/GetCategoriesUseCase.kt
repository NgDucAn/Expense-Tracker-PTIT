package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.CategoryGroup
import com.duongpt.expensetracker.features.money.domain.model.CategoryType
import com.duongpt.expensetracker.features.money.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) : UseCase<Map<CategoryType, List<CategoryGroup>>, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Map<CategoryType, List<CategoryGroup>>> {
        return try {
            val categories = categoryRepository.getAllCategoryGroups().first()
            Either.Right(categories)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
}