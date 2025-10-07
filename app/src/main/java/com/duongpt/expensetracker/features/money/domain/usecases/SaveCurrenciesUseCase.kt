package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.Currency
import com.duongpt.expensetracker.features.money.domain.repository.CurrencyRepository
import javax.inject.Inject

class SaveCurrenciesUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : UseCase<Unit, SaveCurrenciesUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Unit> {
        return try {
            currencyRepository.insertCurrencies(params.currencies)
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val currencies: List<Currency>)
}