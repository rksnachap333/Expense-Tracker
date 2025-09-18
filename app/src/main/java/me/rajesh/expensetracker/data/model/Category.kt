package me.rajesh.expensetracker.data.model

import me.rajesh.expensetracker.data.enums.CategoryEnum

data class Category(
    val category: CategoryEnum,
    val color: Int
){
    override fun toString(): String {
        return category.displayName.replaceFirstChar { it.uppercase() }
    }
}