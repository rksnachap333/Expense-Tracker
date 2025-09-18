package me.rajesh.expensetracker.data.repository.localRepo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.rajesh.expensetracker.data.db.entity.Expense
import me.rajesh.expensetracker.data.db.service.AppDatabaseService
import me.rajesh.expensetracker.data.model.ExpenseCreateDto
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ExpenseRepository @Inject constructor(
    private val appDatabaseService: AppDatabaseService
) {

    suspend fun insertExpense(expenseCreateDto: ExpenseCreateDto) {
        withContext(Dispatchers.IO) {
            appDatabaseService.insertExpense(expenseCreateDto.toEntity())
        }

    }

    fun getAllExpenses(): Flow<List<ExpenseResponseDto>> {
        return appDatabaseService.getAllExpenses()
            .map { expenses ->
                expenses.map { it.toResponseDto() }
            }

    }

    suspend fun updateExpense(newExpense: ExpenseResponseDto): Int {
        return withContext(Dispatchers.IO) {
            appDatabaseService.editExpense(newExpense.toEntity())
        }
    }

    suspend fun deleteExpense(expense: ExpenseResponseDto): Int {
        return withContext(Dispatchers.IO) {
            appDatabaseService.deleteExpense(expense.toEntity())
        }
    }

}