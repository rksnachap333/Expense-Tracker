package me.rajesh.expensetracker.ui.fragments.expense_list

import me.rajesh.expensetracker.data.model.ExpenseResponseDto

sealed class TransactionItem {
    data class Header(val title: String) : TransactionItem()
    data class Item(val expense: ExpenseResponseDto) : TransactionItem()
}