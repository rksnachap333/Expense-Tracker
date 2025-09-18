package me.rajesh.expensetracker.ui.fragments.expense_detail

import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat.getDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import me.rajesh.expensetracker.MainActivity
import me.rajesh.expensetracker.R
import me.rajesh.expensetracker.data.enums.CategoryEnum
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.databinding.FragmentExpenseDetailsBinding
import me.rajesh.expensetracker.di.component.DaggerExpenseDetailComponent
import me.rajesh.expensetracker.ui.base.UiState
import me.rajesh.expensetracker.ui.fragments.add.CategoryAdapter
import me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel.ExpenseViewModel
import me.rajesh.expensetracker.utils.AppConstant
import me.rajesh.expensetracker.utils.ToastUtils
import me.rajesh.expensetracker.utils.getCategory
import me.rajesh.expensetracker.utils.withAlpha
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExpenseDetails : Fragment() {

    private var _binding: FragmentExpenseDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseResponseDto: ExpenseResponseDto
    private val args: ExpenseDetailsArgs by navArgs()


    @Inject
    lateinit var expenseViewModel: ExpenseViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependency()
        super.onCreate(savedInstanceState)
        expenseResponseDto = args.expenseResponseDto
    }

    private fun injectDependency() {
        DaggerExpenseDetailComponent
            .builder()
            .activityComponent((requireActivity() as MainActivity).activityComponent)
            .build().inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExpenseDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("ExpenseDTO : $expenseResponseDto")
        setEventListener()
        setData()
        addObserver()
    }


    private fun setData() {
        setEditableData(expenseResponseDto)
        binding.transactionIdText.text = expenseResponseDto.id.toString()
        binding.dateText.text = getDateFormat(expenseResponseDto.timestamp)

        var category = AppConstant.CATEGORY_LIST[0]
        val categoryEnum = CategoryEnum.Companion.fromDisplayName(expenseResponseDto.category)
        categoryEnum?.let { it: CategoryEnum ->
            category = getCategory(it)
        }

        val color = ContextCompat.getColor(requireActivity(), category.color)

        val bg =
            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_category_chip)?.mutate()
        if (bg is GradientDrawable) {
            bg.setStroke(2, color)
            bg.setColor(color.withAlpha(90)) // light background
        }
        binding.categoryText.background = bg
        binding.categoryText.text = expenseResponseDto.category
        categoryEnum?.let {
            binding.categoryIcon.setImageResource(categoryEnum.icon)
        }

        val imageUriString = expenseResponseDto.file
        if (imageUriString.isNotEmpty()) {
            Glide.with(binding.imageContent).load(imageUriString).into(binding.imageContent)
        }


    }

    private fun setEditableData(expenseResponseDto: ExpenseResponseDto) {
        binding.title.text = expenseResponseDto.title
        binding.amountText.text = "₹${expenseResponseDto.amount}"
        binding.descriptionText.text = expenseResponseDto.notes
        binding.categoryDetailText.text = expenseResponseDto.category
    }

    private fun setEventListener() {
        binding.backButton.setOnClickListener { navigateToBack() }
        binding.deleteExpenseButton.setOnClickListener { deleteExpense() }
        binding.deleteButton.setOnClickListener { deleteExpense() }
        binding.editButton.setOnClickListener { openEditDialog() }
        binding.editExpenseButton.setOnClickListener {
            openEditDialog()
        }

    }

    private fun openEditDialog() {
        showEditExpenseDialog(expenseResponseDto) {
            saveEditedExpense(it)
        }
    }

    private fun navigateToBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun getDateFormat(timestamp: Long): String {

        val dateFormatter = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormatter.format(Date(timestamp))
        return formattedDate
    }

    private fun deleteExpense() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteItem()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteItem() {
        expenseViewModel.deleteExpense(expenseResponseDto)
    }

    private fun saveEditedExpense(it: ExpenseResponseDto) {
        expenseViewModel.updateExpense(it)
    }


    fun showEditExpenseDialog(
        expense: ExpenseResponseDto,
        onSave: (ExpenseResponseDto) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_expense, null)

        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etExpenseTitle)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etAmount)
        val spinnerCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerCategory)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etNotes)

        // Pre-fill existing values
        etTitle.setText(expense.title)
        etAmount.setText(expense.amount.toString())
        spinnerCategory.setText(expense.category, false)
        etNotes.setText(expense.notes ?: "")

        val categories = AppConstant.CATEGORY_LIST
        val adapter = CategoryAdapter(requireContext(), categories)
        spinnerCategory.setAdapter(adapter)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Expense")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedExpense = expense.copy(
                    title = etTitle.text.toString().trim(),
                    amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0,
                    category = spinnerCategory.text.toString(),
                    notes = etNotes.text.toString().trim()
                )
                onSave(updatedExpense)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeUpdate() }
                launch { observeDelete() }
            }
        }


    }

    private suspend fun observeUpdate() {
        expenseViewModel.updateExpenseState.collect {
            when (it) {
                is UiState.Success -> {
                    updateView(it.data)
                }

                is UiState.Error -> {
                    ToastUtils.showError(requireContext(), "Expense Edition Unsuccessful!!")
                }

                is UiState.Loading -> {}
                is UiState.Ignore -> {}
            }
        }
    }

    private fun updateView(updatedData: ExpenseResponseDto) {
        expenseResponseDto = updatedData
        setEditableData(expenseResponseDto)
        ToastUtils.showSuccess(requireContext(), "Expense Edited!!")
    }


    private suspend fun observeDelete() {
        expenseViewModel.deleteExpenseState.collect {
            when (it) {
                is UiState.Success -> {
                    updateDelete()
                }

                is UiState.Error -> {
                    ToastUtils.showError(requireContext(), "Expense Deletion Unsuccessful!!")
                }

                is UiState.Loading -> {}
                is UiState.Ignore -> {}
            }
        }
    }

    private fun updateDelete() {
        ToastUtils.showSuccess(requireContext(), "Expense Deleted!!")
        navigateToBack()
    }

}