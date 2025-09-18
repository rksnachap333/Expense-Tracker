package me.rajesh.expensetracker.data.model

data class TransactionGroup(
    val groupName: String,
    val transactions: List<ExpenseResponseDto>
)
