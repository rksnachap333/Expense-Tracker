package me.rajesh.expensetracker.ui.fragments.add

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.data.model.Category
import me.rajesh.expensetracker.databinding.ItemCategoryBinding
import me.rajesh.expensetracker.utils.withAlpha

class CategoryAdapter(
    private val context: Context,
    private val categories: ArrayList<Category>
) : ArrayAdapter<Category>(context, 0, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createChipView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createChipView(position, parent)
    }

    private fun createChipView(position: Int, parent: ViewGroup): View {
        val category = getItem(position)!!
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val tvCategory = binding.tvCategory
        tvCategory.text = category.category.displayName.replaceFirstChar { it.uppercase() }

        val color = ContextCompat.getColor(context, category.color)
        tvCategory.setTextColor(color)

        val bg = ContextCompat.getDrawable(context, R.drawable.bg_category_chip)?.mutate()
        if (bg is GradientDrawable) {
            bg.setStroke(2, color)
            bg.setColor(color.withAlpha(30)) // light background
        }
        binding.tvCategory.background = bg

        return binding.root
    }


}