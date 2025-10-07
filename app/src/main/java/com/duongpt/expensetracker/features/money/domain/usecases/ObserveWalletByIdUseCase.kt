package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.Wallet
import com.duongpt.expensetracker.features.money.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveWalletByIdUseCase(
    private val walletRepository: WalletRepository
) : UseCase<Flow<Wallet>, ObserveWalletByIdUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Flow<Wallet>> {
        return try {
            val walletId = when (params) {
                is Params.ByWalletId -> params.walletId
            }
            val walletFlow = walletRepository.getWalletById(walletId)
                .map { walletEntity -> walletEntity?.let { Wallet.fromEntity(it) } ?: throw IllegalStateException("Wallet not found") }
            Either.Right(walletFlow)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    sealed class Params {
        data class ByWalletId(val walletId: Int) : Params()
    }
}