package me.rajesh.expensetracker.ui.fragments.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.databinding.TransactionItemCellBinding

class TransactionAdapter(
    private val expenseList: ArrayList<ExpenseResponseDto>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClick: ((ExpenseResponseDto) -> Unit)? = null

    fun setOnItemClickListener(listener: (ExpenseResponseDto) -> Unit) {
        onItemClick = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding =
            TransactionItemCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding,onItemClick)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is TransactionViewHolder) {
            holder.bind(expenseList[position])
        }
    }

    override fun getItemCount() = expenseList.size

    fun addData(list: List<ExpenseResponseDto>){
        expenseList.clear()
        expenseList.addAll(list)
    }
}