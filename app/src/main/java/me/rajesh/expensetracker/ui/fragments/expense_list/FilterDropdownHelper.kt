package me.rajesh.expensetracker.ui.fragments.expense_list

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.rajesh.expensetracker.R
import javax.inject.Inject

class FilterDropdownHelper @Inject constructor(
    private val context: Context
) {

    data class FilterOption(
        val id: String,
        val title: String,
        val isSelected: Boolean = false
    )

    // Date filter options
    private var dateOptions = listOf(
        FilterOption("today", "Today",true),
        FilterOption("this_week", "This Week"),
        FilterOption("this_month", "This Month"),
    )

    // Group filter options
    private var groupOptions = listOf(
        FilterOption("time", "Group by Time", true),
        FilterOption("category", "Group by Category"),
    )

    fun showDateFilterDropdown(
        anchorView: View,
        onOptionSelected: (FilterOption) -> Unit
    ) {
        showDropdown(anchorView, dateOptions, onOptionSelected)
    }

    fun showGroupFilterDropdown(
        anchorView: View,
        onOptionSelected: (FilterOption) -> Unit
    ) {
        showDropdown(anchorView, groupOptions, onOptionSelected)
    }

    private fun showDropdown(
        anchorView: View,
        options: List<FilterOption>,
        onOptionSelected: (FilterOption) -> Unit
    ) {
        val popupWindow = PopupWindow(context)
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.dropdown_menu_layout, null)

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.dropdownRecyclerView)

        var currentOptions = options // keep mutable reference

        val adapter = DropdownAdapter(options) { selectedOption ->

            currentOptions = currentOptions.map {
                it.copy(isSelected = it.id == selectedOption.id)
            }
            if (options === dateOptions) {
                dateOptions = currentOptions
            } else if (options === groupOptions) {
                groupOptions = currentOptions
            }

            onOptionSelected(selectedOption)
            popupWindow.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Configure popup window
        popupWindow.apply {
            contentView = popupView
            width = anchorView.width
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dropdown_background))
            elevation = 8f
        }

        // Calculate position
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)

        // Show below the anchor view
        popupWindow.showAtLocation(
            anchorView,
            Gravity.NO_GRAVITY,
            location[0],
            location[1] + anchorView.height + 8.dpToPx(context)
        )
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}