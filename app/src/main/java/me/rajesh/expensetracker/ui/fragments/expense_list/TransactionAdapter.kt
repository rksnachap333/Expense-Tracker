package me.rajesh.expensetracker.ui.fragments.expense_list

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.data.enums.CategoryEnum
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.databinding.ItemHeaderBinding
import me.rajesh.expensetracker.databinding.ItemTransactionBinding
import me.rajesh.expensetracker.utils.AppConstant.CATEGORY_LIST
import me.rajesh.expensetracker.utils.getCategory
import me.rajesh.expensetracker.utils.withAlpha

class TransactionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClick: ((ExpenseResponseDto) -> Unit)? = null

    fun setOnItemClickListener(listener: (ExpenseResponseDto) -> Unit) {
        onItemClick = listener
    }


    private val items = mutableListOf<TransactionItem>()

    fun submitList(newItems: List<TransactionItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TransactionItem.Header -> 0
            is TransactionItem.Item -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding =
                ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding =
                ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding, onItemClick)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TransactionItem.Header -> (holder as HeaderViewHolder).bind(item)
            is TransactionItem.Item -> (holder as ItemViewHolder).bind(item)
        }
    }

    class HeaderViewHolder(
        private val binding: ItemHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: TransactionItem.Header) {
            binding.tvHeader.text = header.title
        }
    }

    class ItemViewHolder(
        private val binding: ItemTransactionBinding,
        private val onItemClick: ((ExpenseResponseDto) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionItem.Item) {
            binding.transactionTitle.text = item.expense.title
            binding.amountText.text = "₹${item.expense.amount}"

            var category = CATEGORY_LIST[0]
            val categoryEnum = CategoryEnum.fromDisplayName(item.expense.category)
            categoryEnum?.let { it: CategoryEnum ->
                category = getCategory(it)
            }

            val color = ContextCompat.getColor(itemView.context, category.color)

            val bg =
                ContextCompat.getDrawable(itemView.context, R.drawable.bg_category_chip)?.mutate()
            if (bg is GradientDrawable) {
                bg.setStroke(2, color)
                bg.setColor(color.withAlpha(90)) // light background
            }
            binding.tvCategory.background = bg
            binding.tvCategory.text = item.expense.category
            categoryEnum?.let {
                binding.categoryIcon.setImageResource(categoryEnum.icon)
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(item.expense)
            }
        }
    }
}
