package me.rajesh.expensetracker.ui.fragments.home

import android.annotation.SuppressLint
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
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.databinding.FragmentHomeBinding
import me.rajesh.expensetracker.di.component.DaggerHomeFragmentComponent
import me.rajesh.expensetracker.ui.base.UiState
import me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel.ExpenseViewModel
import me.rajesh.expensetracker.ui.fragments.common.viewholder.TransactionAdapter
import me.rajesh.expensetracker.utils.hide
import me.rajesh.expensetracker.utils.show
import javax.inject.Inject


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var expenseViewModel: ExpenseViewModel

    @Inject
    lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependency()
        super.onCreate(savedInstanceState)

        println("Expense ViewMode ===${expenseViewModel.hashCode()}")
    }

    private fun injectDependency() {
        DaggerHomeFragmentComponent
            .builder()
            .activityComponent((requireActivity() as MainActivity).activityComponent)
            .build().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmerContainer.startShimmer()
        binding.shimmerContainer.visibility = View.VISIBLE
        setUpObserver()
        addEventListener()
        setTransactionRecyclerView()
    }


    private fun setUpObserver() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeExpenseList() }
                launch { observeLatestTotalExpense() }
                launch { observeLatestCount() }
            }
        }

    }

    private fun addEventListener() {
        binding.btnAddExpense.setOnClickListener { gotToAddFragment() }
        binding.btnAddFirstExpense.setOnClickListener { gotToAddFragment() }
        binding.btnViewAll.setOnClickListener { gotoListFragment() }
    }

    private fun gotoListFragment() {
        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.expenseListFragment
    }

    private fun gotToAddFragment() {
        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.addFragment
    }

    private suspend fun observeExpenseList() {
        expenseViewModel.weeklyExpenseList.collect { it: UiState<List<ExpenseResponseDto>> ->
            when (it) {
                is UiState.Success -> {
                    renderList(it.data)
                }

                is UiState.Error -> {
                    println("Home Fragment : In error of observeExpenseList")
                }

                is UiState.Loading -> {
                    println("Home Fragment : In loading of observeExpenseList")
                }

                is UiState.Ignore -> {}
            }
        }
    }

    private suspend fun observeLatestCount() {
        expenseViewModel.weeklyTotalExpenseCount.collect {
            when (it) {
                is UiState.Success -> {
                    binding.tvTotalExpenses.text = "${it.data}"
                }

                is UiState.Error -> {
                    println("Home Fragment : In error of observeLatestCount")
                }

                is UiState.Loading -> {
                    println("Home Fragment : Loading observeLatestCount")
                }

                is UiState.Ignore -> {}
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun observeLatestTotalExpense() {
        expenseViewModel.weeklyTotalExpense.collect {
            when (it) {
                is UiState.Success -> {
                    binding.tvThisWeek.text = "₹${it.data}"
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

    private fun setTransactionRecyclerView() {
        binding.entryList.layoutManager = LinearLayoutManager(context)
        binding.entryList.adapter = transactionAdapter
        transactionAdapter.setOnItemClickListener { it: ExpenseResponseDto ->
            gotoDetailPage(it)
        }
    }

    private fun renderList(data: List<ExpenseResponseDto>) {
        binding.shimmerContainer.stopShimmer()
        if (data.isEmpty()) {
            showOnly(binding.noEntryView)
        } else {
            showOnly(binding.entryList)
            transactionAdapter.addData(data)
            transactionAdapter.notifyDataSetChanged()
        }
    }

    private fun showOnly(target: View) {
        listOf(binding.entryList, binding.noEntryView, binding.shimmerContainer)
            .forEach { if (it == target) it.show() else it.hide() }
    }

    private fun gotoDetailPage(expenseResponseDto: ExpenseResponseDto) {
        val action = HomeFragmentDirections.actionHomeFragmentToExpenseDetails(expenseResponseDto)
        findNavController().navigate(action)
    }

}


