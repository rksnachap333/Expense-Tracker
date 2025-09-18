package me.rajesh.expensetracker.ui.fragments.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.rajesh.expensetracker.MainActivity
import me.rajesh.expensetracker.data.enums.DateFilter
import me.rajesh.expensetracker.data.enums.GroupFilter
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.data.model.TransactionGroup
import me.rajesh.expensetracker.databinding.FragmentReportBinding
import me.rajesh.expensetracker.di.component.DaggerReportFragmentComponent
import me.rajesh.expensetracker.ui.base.UiState
import me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel.ExpenseViewModel
import me.rajesh.expensetracker.ui.fragments.expense_list.TransactionAdapter
import me.rajesh.expensetracker.ui.fragments.expense_list.TransactionItem
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.List

class ReportFragment : Fragment() {

    private lateinit var _binding: FragmentReportBinding
    private val binding get() = _binding

    @Inject
    lateinit var expenseViewModel: ExpenseViewModel

    private lateinit var categoryExpenseAdapter: CategoryExpenseAdapter
    private lateinit var weekDailyReportAdapter: WeekDailyReportAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependency()
        super.onCreate(savedInstanceState)
    }

    private fun injectDependency() {
        DaggerReportFragmentComponent
            .builder()
            .activityComponent((requireActivity() as MainActivity).activityComponent)
            .build().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryExpenseAdapter = CategoryExpenseAdapter()
        weekDailyReportAdapter = WeekDailyReportAdapter()
        applyFilters()
        setUpLast7DayRecyclerView()
        setUpCategoryRecyclerView()
        setUpObserver()

    }

    private fun setUpObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeLatestTotalExpense() }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private suspend fun observeLatestTotalExpense() {
        expenseViewModel.weeklyTotalExpense.collect {
            when (it) {
                is UiState.Success -> {
                    binding.last7DaysTotal.text = "₹${it.data}"
                }

                is UiState.Error -> {
                    println("Home Fragment : In error of observeLatestTotalExpense")
                }

                is UiState.Loading -> {
                    println("Home Fragment : In Loading of observeLatestTotalExpense")
                }

                is UiState.Ignore -> {}
            }
        }
    }

    private fun setUpCategoryRecyclerView() {
        binding.expenseCategoryRecycleView.layoutManager = LinearLayoutManager(context)
        binding.expenseCategoryRecycleView.adapter = categoryExpenseAdapter
    }

    private fun setUpLast7DayRecyclerView() {
        binding.dayRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.dayRecyclerView.adapter = weekDailyReportAdapter
    }

    private fun applyFilters() {

        val dateFilterToChange = DateFilter.WEEK
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.getExpensesByFilter(dateFilterToChange)
                    .collect { it: UiState<List<ExpenseResponseDto>> ->
                        when (it) {
                            is UiState.Error -> {}
                            is UiState.Loading -> {}
                            is UiState.Ignore -> {}
                            is UiState.Success -> {
                                val data = it.data

                                val groupedByTime = groupTransactionsByTime(data)
                                val groupedByCategory = groupTransactionsByCategory(data)

//                                val displayListOfWeek = prepareTimeList(groupedByTime)
//                                val displayListOfCategory = prepareCategoryList(groupedByCategory)

//                            renderData(displayList)
                                renderWeekDayItem(groupedByTime)
                                renderCategoryItems(groupedByCategory)

                            }
                        }
                    }
            }
        }

    }


    private fun groupTransactionsByTime(transactions: List<ExpenseResponseDto>): List<TransactionGroup> {
        val last7Days = (0..6).map { offset ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -offset)

            val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Sun"
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> ""
            }

            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH) + 1

            "$dayOfWeek"
        }.reversed() // oldest → newest

        val grouped = transactions.groupBy { it: ExpenseResponseDto ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.timestamp
            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Sun"
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> ""
            }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            "$dayOfWeek"
        }

        return last7Days.map { dayKey ->
            TransactionGroup(dayKey, grouped[dayKey] ?: emptyList())
        }
    }


    private fun groupTransactionsByCategory(transactions: List<ExpenseResponseDto>): List<TransactionGroup> {
        return transactions.groupBy { it.category }
            .map { TransactionGroup(it.key, it.value) }
    }

    private fun prepareCategoryList(grouped: List<TransactionGroup>): List<TransactionItem> {
        val list = mutableListOf<TransactionItem>()
        grouped.forEach { group ->
            list.add(TransactionItem.Header(group.groupName)) // category name
            group.transactions.forEach { txn ->
                list.add(TransactionItem.Item(txn))
            }
        }
        return list
    }

    private fun prepareTimeList(grouped: List<TransactionGroup>): List<TransactionItem> {
        val list = mutableListOf<TransactionItem>()
        grouped.forEach { group ->
            // Optional: add date header
            list.add(TransactionItem.Header(group.groupName))
            group.transactions.forEach { txn ->
                list.add(TransactionItem.Item(txn))
            }
        }
        return list
    }


    private fun renderCategoryItems(groupedByCategory: List<TransactionGroup>) {
        categoryExpenseAdapter.submitList(groupedByCategory)
    }

    private fun renderWeekDayItem(groupedByCategory: List<TransactionGroup>) {
        weekDailyReportAdapter.submitList(groupedByCategory)
    }
}