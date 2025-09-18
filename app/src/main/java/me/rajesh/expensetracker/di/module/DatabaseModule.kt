package me.rajesh.expensetracker.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import me.rajesh.expensetracker.data.db.AppDatabase
import me.rajesh.expensetracker.data.db.dao.ExpenseDao
import me.rajesh.expensetracker.data.db.service.AppDatabaseService
import me.rajesh.expensetracker.data.db.service.DatabaseService
import me.rajesh.expensetracker.di.ApplicationContext
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "expense_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    @Singleton
    fun provideDatabaseService(appDatabase: AppDatabase): DatabaseService {
        return AppDatabaseService(appDatabase)
    }

}