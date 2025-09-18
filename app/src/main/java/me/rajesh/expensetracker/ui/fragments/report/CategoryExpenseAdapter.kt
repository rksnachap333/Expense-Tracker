package me.rajesh.expensetracker.ui.fragments.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.rajesh.expensetracker.data.enums.CategoryEnum
import me.rajesh.expensetracker.data.model.TransactionGroup
import me.rajesh.expensetracker.databinding.ReportCategoryItemBinding

class CategoryExpenseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<TransactionGroup>()
    private var totalAmount = items.sumOf { it.transactions.sumOf { it.amount } }

    fun submitList(newItems: List<TransactionGroup>) {
        items.clear()
        items.addAll(newItems)
        totalAmount = items.sumOf { it.transactions.sumOf { it.amount } }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding =
            ReportCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as ItemViewHolder).bind(items[position], totalAmount)
    }

    override fun getItemCount(): Int = items.size

    class ItemViewHolder(private val binding: ReportCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionGroup, totalAmount: Double) {
            binding.expenseOn.text = item.groupName
            binding.expenseCount.text = "${item.transactions.size} expense"
            binding.amount.text = "₹${item.transactions.sumOf { it.amount }}"
            val categoryEnum = CategoryEnum.fromDisplayName(item.groupName)
            categoryEnum?.let {
                binding.categoryIcon.setImageResource(it.icon)
            }

            val percentage = if (totalAmount > 0) {
                (item.transactions.sumOf { it.amount } / totalAmount) * 100
            } else 0.0

            binding.myProgressBar.progress = percentage.toInt()

        }
    }
}