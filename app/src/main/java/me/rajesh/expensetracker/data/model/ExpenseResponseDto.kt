package me.rajesh.expensetracker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.rajesh.expensetracker.data.db.entity.Expense


@Parcelize
data class ExpenseResponseDto(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String,
    val timestamp: Long,
    val file: String
) : Parcelable {
    fun toEntity() : Expense {
        return Expense(
            id = id,
            title = title,
            amount = amount,
            category = category,
            notes = notes,
            timestamp = timestamp,
            file = file
        )
    }
}