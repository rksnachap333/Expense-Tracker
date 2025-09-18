package me.rajesh.expensetracker.ui.fragments.expense_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import me.rajesh.expensetracker.MainActivity
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.data.enums.DateFilter
import me.rajesh.expensetracker.data.enums.GroupFilter
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.data.model.TransactionGroup
import me.rajesh.expensetracker.databinding.FragmentExpenseListBinding
import me.rajesh.expensetracker.di.component.DaggerExpenseListComponent
import me.rajesh.expensetracker.ui.base.UiState
import me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel.ExpenseViewModel
import me.rajesh.expensetracker.ui.fragments.home.HomeFragmentDirections
import me.rajesh.expensetracker.utils.hide
import me.rajesh.expensetracker.utils.show
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var filterManager: FilterManager

    @Inject
    lateinit var expenseViewModel: ExpenseViewModel

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependency()
        super.onCreate(savedInstanceState)

    }

    private fun injectDependency() {
        DaggerExpenseListComponent
            .builder()
            .activityComponent((requireActivity() as MainActivity).activityComponent)
            .build().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionAdapter = TransactionAdapter()
        setUpRecyclerView()
        binding.shimmerContainer.startShimmer()
        binding.shimmerContainer.visibility = View.VISIBLE
        setupFilters()
        applyFilters("Today", "Group by Time")
        setEventListener()

    }

    private fun setEventListener() {
        binding.topAppBar.setNavigationOnClickListener { navigateToHome() }
    }


    private fun navigateToHome() {
        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.homeFragment
    }


    private fun setupFilters() {
        filterManager = FilterManager(requireContext()) { dateFilter, groupFilter ->
            // Handle filter changes
            applyFilters(dateFilter, groupFilter)
        }

        filterManager.setupFilterButtons(
            binding.btnFilterDate,
            binding.btnGroupBy
        )
    }

    private fun applyFilters(dateFilter: String, groupFilter: String) {

        val dateFilterToChange = when (dateFilter) {
            "Today" -> DateFilter.TODAY
            "This Week" -> DateFilter.WEEK
            "This Month" -> DateFilter.MONTH
            else -> DateFilter.TODAY
        }

        val groupFilterToChange = when (groupFilter) {
            "Group by Time" -> GroupFilter.TIME
            "Group by Category" -> GroupFilter.CATEGORY
            else -> GroupFilter.TIME
        }

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

                                // i have grouped data now i can show this in adapter
                                val grouped = when (groupFilterToChange) {
                                    GroupFilter.TIME -> groupTransactionsByTime(data)
                                    GroupFilter.CATEGORY -> groupTransactionsByCategory(data)
                                }

                                val displayList = when (groupFilterToChange) {
                                    GroupFilter.TIME -> prepareTimeList(grouped) // continuous or optional header
                                    GroupFilter.CATEGORY -> prepareCategoryList(grouped) // category header + items
                                }
                                displayNumbers(it.data)
                                renderData(displayList)
                            }
                        }
                    }
            }
        }

    }


    private fun groupTransactionsByTime(transactions: List<ExpenseResponseDto>): List<TransactionGroup> {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

        return transactions.groupBy { transaction ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = transaction.timestamp
            }

            val isToday = cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

            val isYesterday = cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)

            when {
                isToday -> "Today"
                isYesterday -> "Yesterday"
                else -> dateFormatter.format(Date(transaction.timestamp))
            }
        }.map { (label, items) ->
            TransactionGroup(label, items)
        }
    }


    private fun groupTransactionsByCategory(transactions: List<ExpenseResponseDto>): List<TransactionGroup> {
        return transactions.groupBy { it.category }
            .map { TransactionGroup(it.key, it.value) }
    }


    private fun setUpRecyclerView() {
        binding.expenseRecycler.layoutManager = LinearLayoutManager(context)
        binding.expenseRecycler.adapter = transactionAdapter
        transactionAdapter.setOnItemClickListener {
            gotoDetailPage(it)
        }
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


    private fun renderData(displayList: List<TransactionItem>) {
        binding.shimmerContainer.stopShimmer()
        if(displayList.isNullOrEmpty()) {
            showOnly(binding.noRecordView)
            return
        }
        showOnly(binding.expenseRecycler)
        transactionAdapter.submitList(displayList)
    }

    private fun displayNumbers(data: List<ExpenseResponseDto>) {
        binding.tvTotalExpenses.text = data.size.toString()
        binding.tvTotalAmount.text = data.sumOf { it.amount }.toString()
    }

    private fun showOnly(target: View) {
        listOf(binding.expenseRecycler, binding.shimmerContainer, binding.noRecordView)
            .forEach { if (it == target) it.show() else it.hide() }
    }


    private fun gotoDetailPage(expenseResponseDto: ExpenseResponseDto) {
        val action = ExpenseListFragmentDirections.actionExpenseListFragmentToExpenseDetails(expenseResponseDto)
        findNavController().navigate(action)
    }


}