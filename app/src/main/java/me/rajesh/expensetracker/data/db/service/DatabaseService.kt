package me.rajesh.expensetracker.data.db.service

import kotlinx.coroutines.flow.Flow
import me.rajesh.expensetracker.data.db.entity.Expense

interface DatabaseService {

    fun insertExpense(expense : Expense)

    fun getAllExpenses() : Flow<List<Expense>>

    fun editExpense(newExpense : Expense) : Int

    fun deleteExpense(expense: Expense) : Int
}