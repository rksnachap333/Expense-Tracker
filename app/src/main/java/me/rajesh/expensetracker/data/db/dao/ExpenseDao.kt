package me.rajesh.expensetracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.rajesh.expensetracker.data.db.entity.Expense

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insert(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Expense>>

    @Update
    fun update(expense: Expense): Int

    @Delete
    fun delete(expense: Expense) : Int

}