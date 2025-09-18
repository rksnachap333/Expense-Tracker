package me.rajesh.expensetracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.rajesh.expensetracker.data.db.dao.ExpenseDao
import me.rajesh.expensetracker.data.db.entity.Expense

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}