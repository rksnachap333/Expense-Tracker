package me.rajesh.expensetracker.ui.fragments.common.viewholder

import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.data.enums.CategoryEnum
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.databinding.TransactionItemCellBinding
import me.rajesh.expensetracker.utils.AppConstant.CATEGORY_LIST
import me.rajesh.expensetracker.utils.getCategory
import me.rajesh.expensetracker.utils.withAlpha

class TransactionViewHolder(
    private val binding: TransactionItemCellBinding,
    private val onItemClick:((ExpenseResponseDto)-> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(expenseResponseDto: ExpenseResponseDto) {
        binding.transactionTitle.text = expenseResponseDto.title
        binding.amountText.text = expenseResponseDto.amount.toString()
        var category = CATEGORY_LIST[0]
        val categoryEnum = CategoryEnum.fromDisplayName(expenseResponseDto.category.lowercase())
        categoryEnum?.let { it: CategoryEnum ->
            category = getCategory(it)
        }

        val color = ContextCompat.getColor(binding.root.context, category.color)

        val bg =
            ContextCompat.getDrawable(binding.root.context, R.drawable.bg_category_chip)?.mutate()
        if (bg is GradientDrawable) {
            bg.setStroke(2, color)
            bg.setColor(color.withAlpha(90)) // light background
        }
        binding.tvCategory.background = bg
        binding.tvCategory.text = expenseResponseDto.category.replaceFirstChar { it.uppercase() }
        categoryEnum?.let {
            binding.categoryIcon.setImageResource(categoryEnum.icon)
        }
        binding.root.setOnClickListener {
            onItemClick?.invoke(expenseResponseDto)
        }
    }


}