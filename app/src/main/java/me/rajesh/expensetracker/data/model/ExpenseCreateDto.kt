package me.rajesh.expensetracker.data.model

import me.rajesh.expensetracker.data.db.entity.Expense

data class ExpenseCreateDto(
    val title: String,
    val amount: Double,
    val category: String,
    val notes : String = "",
    val timestamp: Long,
    val file : String = ""
) {
    fun toEntity() : Expense {
        return Expense(
            title = title,
            amount = amount,
            category = category,
            notes = notes,
            timestamp = timestamp,
            file = file
        )
    }
}