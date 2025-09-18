package me.rajesh.expensetracker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.rajesh.expensetracker.data.model.ExpenseResponseDto

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val timestamp: Long,
    val notes: String = "",
    val file: String = ""
) {
    fun toResponseDto(): ExpenseResponseDto {
        return ExpenseResponseDto(
            id = this.id,
            title = this.title,
            amount = this.amount,
            category = this.category,
            notes = this.notes,
            timestamp = this.timestamp,
            file = this.file
        )
    }
}