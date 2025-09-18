package me.rajesh.expensetracker.utils

import android.graphics.Color
import android.view.View
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.data.enums.CategoryEnum
import me.rajesh.expensetracker.data.model.Category
import me.rajesh.expensetracker.utils.AppConstant.CATEGORY_LIST

object AppConstant {

    val CATEGORY_LIST = arrayListOf<Category>(
        Category(CategoryEnum.EDUCATION, R.color.category_education),
        Category(CategoryEnum.STAFF, R.color.category_staff),
        Category(CategoryEnum.TRAVEL, R.color.category_travel),
        Category(CategoryEnum.FOOD, R.color.category_food),
        Category(CategoryEnum.UTILITY, R.color.category_utilities),
        Category(CategoryEnum.ENTERTAINMENT, R.color.category_entertainment),
    )

}

fun Int.withAlpha(alpha: Int): Int {
    return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun getCategory(enum: CategoryEnum): Category {
    return CATEGORY_LIST.find { it.category == enum } ?: CATEGORY_LIST[0]
}

