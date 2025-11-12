package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.data.data_source.local.model.WalletWithCurrencyEntity
import com.ptit.expensetracker.features.money.domain.model.Wallet
import com.ptit.expensetracker.features.money.domain.repository.WalletRepository
import javax.inject.Inject

class CreateWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) : UseCase<Unit, CreateWalletUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Unit> {
        return try {
            walletRepository.insertWallet(
                Wallet(
                    id = params.walletWithCurrency.wallet.id,
                    walletName = params.walletWithCurrency.wallet.walletName,
                    currentBalance = params.walletWithCurrency.wallet.currentBalance,
                    currency = params.walletWithCurrency.currency.let {
                        com.ptit.expensetracker.features.money.domain.model.Currency(
                            id = it.id,
                            currencyName = it.currencyName,
                            currencyCode = it.currencyCode,
                            symbol = it.symbol,
                            displayType = it.displayType,
                            image = it.image
                        )
                    },
                    isMainWallet = params.walletWithCurrency.wallet.isMainWallet,
                )
            )
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val walletWithCurrency: WalletWithCurrencyEntity)
}