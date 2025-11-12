package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.Currency
import com.ptit.expensetracker.features.money.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllCurrenciesUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : UseCase<Flow<List<Currency>>, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Flow<List<Currency>>> {
        return try {
            val currenciesFlow: Flow<List<Currency>> = currencyRepository.getAllCurrencies()
                .map { entities ->
                    entities.map { entity ->
                        Currency(
                            id = entity.id,
                            currencyName = entity.currencyName,
                            currencyCode = entity.currencyCode,
                            symbol = entity.symbol,
                            displayType = entity.displayType,
                            image = entity.image
                        )
                    }
                }
            Either.Right(currenciesFlow)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
} 