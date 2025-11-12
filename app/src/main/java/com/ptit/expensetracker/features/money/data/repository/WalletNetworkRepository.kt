package com.ptit.expensetracker.features.money.data.repository

import com.ptit.expensetracker.core.network.NetworkHandler
import com.ptit.expensetracker.features.money.data.data_source.local.dao.WalletDao
import com.ptit.expensetracker.features.money.data.data_source.local.model.WalletWithCurrencyEntity
import com.ptit.expensetracker.features.money.domain.model.Wallet
import com.ptit.expensetracker.features.money.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow

//class WalletNetworkRepository(
//    private val walletDao: WalletDao,
//    private val networkHandler: NetworkHandler
//): WalletRepository {
//    override suspend fun getWallets(): Flow<List<WalletWithCurrencyEntity>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun insertWallet(wallet: Wallet) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getWalletById(walletId: Int): Flow<WalletWithCurrencyEntity?> {
//        TODO("Not yet implemented")
//    }
//}