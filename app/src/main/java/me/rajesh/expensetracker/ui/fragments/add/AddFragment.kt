package me.rajesh.expensetracker.ui.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.rajesh.expensetracker.MainActivity
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.data.model.Category
import me.rajesh.expensetracker.data.model.ExpenseCreateDto
import me.rajesh.expensetracker.databinding.FragmentAddBinding
import me.rajesh.expensetracker.di.component.DaggerAddFragmentComponent
import me.rajesh.expensetracker.ui.base.UiState
import me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel.ExpenseViewModel
import me.rajesh.expensetracker.utils.AppConstant
import me.rajesh.expensetracker.utils.ToastUtils
import javax.inject.Inject

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var expenseViewModel: ExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependency()
        super.onCreate(savedInstanceState)
        println("Expense ViewMode ===${expenseViewModel.hashCode()}")
    }

    private fun injectDependency() {
        DaggerAddFragmentComponent
            .builder()
            .activityComponent((requireActivity() as MainActivity).activityComponent)
            .build().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObserver()
        setUpCategorySpinner()
        setUpEventListener()
    }


    private fun setUpCategorySpinner() {
        val categories = AppConstant.CATEGORY_LIST
        val adapter = CategoryAdapter(requireContext(), categories)
        binding.spinnerCategory.setAdapter(adapter)

        binding.spinnerCategory.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as Category
        }
    }

    private fun setUpEventListener() {
        binding.btnAddExpense.setOnClickListener {
            validateAndAddExpense()
        }

        binding.topAppBar.setNavigationOnClickListener { navigateToHome() }
    }

    private fun validateAndAddExpense() {
        val title = binding.etExpenseTitle.text?.toString()?.trim()
        val amountText = binding.etAmount.text?.toString()?.trim()
        val category = binding.spinnerCategory.text?.toString()?.trim()

        var isValid = true

        if (title.isNullOrEmpty()) {
            binding.etExpenseTitle.error = "Title is required"
            isValid = false
        } else {
            binding.etExpenseTitle.error = null
        }

        val amount = amountText?.toDoubleOrNull()
        if (amountText.isNullOrEmpty()) {
            binding.etAmount.error = "Amount is required"
            isValid = false
        } else if (amount == null || amount <= 0) {
            binding.etAmount.error = "Enter a valid amount"
            isValid = false
        } else {
            binding.etAmount.error = null
        }

        if (category.isNullOrEmpty()) {
            binding.spinnerCategory.error = "Category is required"
            isValid = false
        } else {
            binding.spinnerCategory.error = null
        }
        if (!isValid) return

        val notes = binding.etNotes.text.toString().trim().ifEmpty { "" }

        val titleNonNull: String = title ?: return
        val amountNonNull: Double = amount ?: return
        val categoryNonNull: String = category ?: return

        expenseViewModel.addExpense(
            ExpenseCreateDto(
                title = titleNonNull,
                amount = amountNonNull,
                category = categoryNonNull,
                notes = notes,
                timestamp = System.currentTimeMillis(),
                file = ""
            )
        )


    }

    private fun addObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { expenseViewModel.addExpenseState.collect { changeExpenseStat(it) } }
                launch { expenseViewModel.todayTotalExpense.collect { changeTodayExpense(it) } }
            }
        }

    }

    private fun changeTodayExpense(it: UiState<Double>) {
        when (it) {
            is UiState.Success -> {
                binding.tvTotalSpent.text = "₹${it.data}"
            }

            is UiState.Error -> {
            }

            is UiState.Loading -> {

            }

            is UiState.Ignore -> {

            }
        }
    }

    private fun changeExpenseStat(it: UiState<ExpenseCreateDto>) {
        when (it) {
            is UiState.Success -> {
                clearAllField()
                ToastUtils.showSuccess(requireContext(), "Expense Added!!")
            }

            is UiState.Error -> {
                ToastUtils.showError(requireContext(), it.message)
            }

            is UiState.Loading -> {

            }

            is UiState.Ignore -> {

            }
        }
    }

    private fun clearAllField() {
        binding.etExpenseTitle.text?.clear()
        binding.etAmount.text?.clear()
        binding.etNotes.text?.clear()
        binding.spinnerCategory.setText("")
    }

    private fun navigateToHome() {
        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.homeFragment
    }
}