package me.rajesh.expensetracker.ui.fragments.expense_list

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.databinding.DropdownMenuItemBinding

class DropdownAdapter(
    private var options: List<FilterDropdownHelper.FilterOption>,
    private val onItemClick: (FilterDropdownHelper.FilterOption) -> Unit
) : RecyclerView.Adapter<DropdownAdapter.DropdownViewHolder>() {

    inner class DropdownViewHolder(
        private val binding: DropdownMenuItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: FilterDropdownHelper.FilterOption) {
            binding.apply {
                menuItemText.text = option.title

                // Show check icon if selected
                checkIcon.visibility = if (option.isSelected) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

                // Set text color for selected item
                menuItemText.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (option.isSelected) R.color.colorPrimary else R.color.onSurface
                    )
                )

                // Set font weight for selected item
                menuItemText.typeface = if (option.isSelected) {
                    Typeface.DEFAULT_BOLD
                } else {
                    Typeface.DEFAULT
                }

                root.setOnClickListener {
                    options = options.map {
                        it.copy(isSelected = it.id == option.id)
                    }
                    notifyDataSetChanged()
                    onItemClick(option)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropdownViewHolder {
        val binding = DropdownMenuItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DropdownViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DropdownViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size
}