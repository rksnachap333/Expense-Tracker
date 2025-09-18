package me.rajesh.expensetracker.data.enums

import androidx.annotation.DrawableRes
import me.rajesh.expensetracker.R

enum class CategoryEnum(
    val displayName: String,
    @DrawableRes val icon: Int
) {
    EDUCATION("education", R.drawable.education),
    STAFF("staff", R.drawable.staff),
    TRAVEL("travel", R.drawable.travel),
    FOOD("food", R.drawable.food),
    UTILITY("utility", R.drawable.utility),
    ENTERTAINMENT("entertainment", R.drawable.entertainment);


    companion object {
        fun fromDisplayName(name: String): CategoryEnum? {
            return entries.find { it.displayName.lowercase().equals(name.lowercase(), ignoreCase = true) }
        }
    }

}