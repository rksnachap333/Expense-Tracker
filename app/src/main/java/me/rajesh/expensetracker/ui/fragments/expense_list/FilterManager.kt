package me.rajesh.expensetracker.ui.fragments.expense_list

import android.content.Context
import com.google.android.material.button.MaterialButton

class FilterManager(
    private val context: Context,
    private val onFiltersChanged: (String, String) -> Unit
) {

    private val dropdownHelper = FilterDropdownHelper(context)
    private var selectedDateFilter = "Today"
    private var selectedGroupFilter = "Group by Time"

    fun setupFilterButtons(dateButton: MaterialButton, groupButton: MaterialButton) {

        // Date filter button click
        dateButton.setOnClickListener {
            dropdownHelper.showDateFilterDropdown(dateButton) { selectedOption ->
                selectedDateFilter = selectedOption.title
                dateButton.text = selectedOption.title
                onFiltersChanged(selectedDateFilter, selectedGroupFilter)
            }
        }

        // Group filter button click
        groupButton.setOnClickListener {
            dropdownHelper.showGroupFilterDropdown(groupButton) { selectedOption ->
                selectedGroupFilter = selectedOption.title
                groupButton.text = selectedOption.title
                onFiltersChanged(selectedDateFilter, selectedGroupFilter)
            }
        }

        // Set initial text
        dateButton.text = selectedDateFilter
        groupButton.text = selectedGroupFilter
    }
}