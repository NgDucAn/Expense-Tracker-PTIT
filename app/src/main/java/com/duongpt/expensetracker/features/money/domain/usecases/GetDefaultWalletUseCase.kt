package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.Wallet
import com.duongpt.expensetracker.features.money.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDefaultWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) : UseCase<Flow<Wallet?>, UseCase.None>() {
    
    override suspend fun run(params: None): Either<Failure, Flow<Wallet?>> {
        return try {
            val defaultWalletFlow = walletRepository.getDefaultWallet().map { walletEntity ->
                walletEntity?.let {
                    Wallet.fromEntity(walletEntity)
                }
            }
            Either.Right(defaultWalletFlow)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
    

} 