package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.data.data_source.local.model.WalletEntity
import com.duongpt.expensetracker.features.money.domain.model.Wallet
import com.duongpt.expensetracker.features.money.domain.repository.WalletRepository
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