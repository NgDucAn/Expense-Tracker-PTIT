package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.duongpt.expensetracker.features.money.domain.model.Wallet

class GetWalletsUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) : UseCase<Flow<List<Wallet>>, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Flow<List<Wallet>>> {
        return try {
            val wallets = walletRepository.getWallets()
                .map { walletEntities ->
                    walletEntities.map { Wallet.fromEntity(it) }
                }
            Either.Right(wallets)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
}