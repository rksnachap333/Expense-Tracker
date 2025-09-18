package me.rajesh.expensetracker.data.db.service

import kotlinx.coroutines.flow.Flow
import me.rajesh.expensetracker.data.db.AppDatabase
import me.rajesh.expensetracker.data.db.entity.Expense
import javax.inject.Inject

class AppDatabaseService @Inject constructor(
    private val appDatabase: AppDatabase
) : DatabaseService{
    override fun insertExpense(expense: Expense) {
        return appDatabase.expenseDao().insert(expense)
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return appDatabase.expenseDao().getAll()
    }

    override fun editExpense(newExpense: Expense): Int {
        return appDatabase.expenseDao().update(newExpense)
    }

    override fun deleteExpense(expense: Expense) : Int {
        return appDatabase.expenseDao().delete(expense)
    }
}