package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.repository.CurrencyRepository
import javax.inject.Inject

class CheckCurrenciesExistUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : UseCase<Boolean, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Boolean> {
        return try {
            val count = currencyRepository.getCurrenciesCount()
            Either.Right(count > 0)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
}