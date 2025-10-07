package com.duongpt.expensetracker.features.money.data.data_source.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.BudgetDao
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.WalletDao
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.CurrencyDao
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.TransactionDao
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.CategoryDao
import com.duongpt.expensetracker.features.money.data.data_source.local.model.BudgetEntity
import com.duongpt.expensetracker.features.money.data.data_source.local.model.CategoryEntity
import com.duongpt.expensetracker.features.money.data.data_source.local.model.CurrencyEntity
import com.duongpt.expensetracker.features.money.data.data_source.local.model.TransactionEntity
import com.duongpt.expensetracker.features.money.data.data_source.local.model.WalletEntity

@Database(
    entities = [WalletEntity::class, CurrencyEntity::class, TransactionEntity::class, CategoryEntity::class, BudgetEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract val walletDao: WalletDao
    abstract val currencyDao: CurrencyDao
    abstract val transactionDao: TransactionDao
    abstract val categoryDao: CategoryDao
    abstract val budgetDao: BudgetDao

    companion object {
        const val DATABASE_NAME = "expense_tracker_database.db"

        @Volatile
        private var Instance: LocalDatabase? = null

        /**
         * Migration from version 1 to 2: Add debt tracking fields to transactions table
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add debt tracking fields to transactions table
                database.execSQL("ALTER TABLE transactions ADD COLUMN parentDebtId INTEGER")
                database.execSQL("ALTER TABLE transactions ADD COLUMN debtReference TEXT")
                database.execSQL("ALTER TABLE transactions ADD COLUMN debtMetadata TEXT")
                
                // Create indexes for better query performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_debtReference ON transactions(debtReference)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_parentDebtId ON transactions(parentDebtId)")
            }
        }

        fun getDatabase(context: Context): LocalDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
//                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}