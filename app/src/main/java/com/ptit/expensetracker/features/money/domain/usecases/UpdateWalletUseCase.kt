package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.data.data_source.local.model.WalletEntity
import com.ptit.expensetracker.features.money.domain.model.Wallet
import com.ptit.expensetracker.features.money.domain.repository.WalletRepository
import javax.inject.Inject

class UpdateWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) : UseCase<Unit, UpdateWalletUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Unit> {
        return try {
            walletRepository.updateWallet(params.wallet)
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    data class Params(val wallet: WalletEntity)
} 