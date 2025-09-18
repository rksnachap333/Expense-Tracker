package me.rajesh.expensetracker.ui.fragments.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.rajesh.expensetracker.data.model.TransactionGroup
import me.rajesh.expensetracker.databinding.ReportDailyItemBinding

class WeekDailyReportAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            ReportDailyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as ItemViewHolder).bind(items[position], totalAmount)
    }

    override fun getItemCount(): Int = items.size

    class ItemViewHolder(
        private val binding: ReportDailyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionGroup,totalAmount: Double) {

            binding.dayText.text = item.groupName
            binding.amount.text = "₹${item.transactions.sumOf { it.amount }}"
            binding.expenseCount.text = "${item.transactions.size} expenses"

            val percentage = if (totalAmount > 0) {
                (item.transactions.sumOf { it.amount } / totalAmount) * 100
            } else 0.0

            binding.myProgressBar.progress = percentage.toInt()


        }
    }
}