package me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import me.rajesh.expensetracker.data.enums.DateFilter
import me.rajesh.expensetracker.data.model.ExpenseCreateDto
import me.rajesh.expensetracker.data.model.ExpenseResponseDto
import me.rajesh.expensetracker.data.repository.localRepo.ExpenseRepository
import me.rajesh.expensetracker.ui.base.UiState
import me.rajesh.expensetracker.utils.getEndOfDay
import me.rajesh.expensetracker.utils.getEndOfWeek
import me.rajesh.expensetracker.utils.getStartOfDay
import me.rajesh.expensetracker.utils.getStartOfWeek
import javax.inject.Inject

class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _totalExpense = MutableStateFlow<UiState<List<ExpenseResponseDto>>>(UiState.Loading)
    val totalExpense: StateFlow<UiState<List<ExpenseResponseDto>>> get() = _totalExpense

    // Weekly Data
    private val _weeklyExpenseList =
        MutableStateFlow<UiState<List<ExpenseResponseDto>>>(UiState.Loading)
    val weeklyExpenseList: StateFlow<UiState<List<ExpenseResponseDto>>> get() = _weeklyExpenseList

    private val _weeklyTotalExpense = MutableStateFlow<UiState<Double>>(UiState.Loading)
    val weeklyTotalExpense: StateFlow<UiState<Double>> get() = _weeklyTotalExpense

    private val _weeklyTotalExpenseCount = MutableStateFlow<UiState<Int>>(UiState.Loading)
    val weeklyTotalExpenseCount: StateFlow<UiState<Int>> get() = _weeklyTotalExpenseCount

    // Today Data

    private val _todayExpenseList =
        MutableStateFlow<UiState<List<ExpenseResponseDto>>>(UiState.Loading)
    val todayExpenseList: StateFlow<UiState<List<ExpenseResponseDto>>> get() = _todayExpenseList

    private val _todayTotalExpense = MutableStateFlow<UiState<Double>>(UiState.Loading)
    val todayTotalExpense: StateFlow<UiState<Double>> get() = _todayTotalExpense

    private val _todayTotalExpenseCount = MutableStateFlow<UiState<Int>>(UiState.Loading)
    val todayTotalExpenseCount: StateFlow<UiState<Int>> get() = _todayTotalExpenseCount


    // Adding Expense State
    private val _addExpenseState = MutableStateFlow<UiState<ExpenseCreateDto>>(UiState.Ignore)
    val addExpenseState: StateFlow<UiState<ExpenseCreateDto>> get() = _addExpenseState

    // Update Expense State
    private val _updateExpenseState = MutableStateFlow<UiState<ExpenseResponseDto>>(UiState.Ignore)
    val updateExpenseState: StateFlow<UiState<ExpenseResponseDto>> get() = _updateExpenseState


    // Delete Expense State
    private val _deleteExpenseState = MutableStateFlow<UiState<ExpenseResponseDto>>(UiState.Ignore)
    val deleteExpenseState: StateFlow<UiState<ExpenseResponseDto>> get() = _deleteExpenseState

    init {
        fetchAllExpenses()
    }

    fun fetchAllExpenses() {
        viewModelScope.launch {
            expenseRepository.getAllExpenses()
                .catch { e ->
                    _totalExpense.value = UiState.Error(e.toString())
                }
                .collect {
                    _totalExpense.value = UiState.Success(it)
                    updateWeeklyReport(it)
                    updateTodayReport(it)
                }
        }

    }

    fun addExpense(expenseCreateDto: ExpenseCreateDto) {
        viewModelScope.launch {
            try {
                expenseRepository.insertExpense(expenseCreateDto)
                _addExpenseState.value = UiState.Success(expenseCreateDto)
            } catch (e: Exception) {
                _addExpenseState.value = UiState.Error(e.toString())
            }

        }

    }

    private fun updateWeeklyReport(totalList: List<ExpenseResponseDto>) {
        val filterWeekList = filterWeekList(totalList)
        _weeklyExpenseList.value = UiState.Success(filterWeekList)
        _weeklyTotalExpense.value = filterWeekList.sumOf { it.amount }.let { UiState.Success(it) }
        _weeklyTotalExpenseCount.value = filterWeekList.count().let { UiState.Success(it) }

    }

    private fun updateTodayReport(totalList: List<ExpenseResponseDto>) {
        val filterTodayList = filterTodayList(totalList)
        _todayExpenseList.value = UiState.Success(filterTodayList)
        _todayTotalExpense.value = filterTodayList.sumOf { it.amount }.let { UiState.Success(it) }
        _todayTotalExpenseCount.value = filterTodayList.count().let { UiState.Success(it) }
    }

    private fun filterWeekList(expenses: List<ExpenseResponseDto>): List<ExpenseResponseDto> {
        val weekStart = getStartOfWeek()
        val weekEnd = getEndOfWeek()
        val weekList = expenses
            .filter { it.timestamp in weekStart..weekEnd }
        return weekList
    }

    private fun filterTodayList(expenses: List<ExpenseResponseDto>): List<ExpenseResponseDto> {
        val todayStart = getStartOfDay()
        val todayEnd = getEndOfDay()
        val todayList = expenses
            .filter { it.timestamp in todayStart..todayEnd }

        return todayList

    }

    fun getExpensesByFilter(dateFilter: DateFilter): StateFlow<UiState<List<ExpenseResponseDto>>> {
        return when (dateFilter) {
            DateFilter.TODAY -> todayExpenseList
            DateFilter.WEEK -> weeklyExpenseList
            DateFilter.MONTH -> totalExpense // if you treat "month" as total
        }
    }

    fun getTotalExpenseByFilter(dateFilter: DateFilter): StateFlow<UiState<Double>> {
        return when (dateFilter) {
            DateFilter.TODAY -> todayTotalExpense
            DateFilter.WEEK -> weeklyTotalExpense
            DateFilter.MONTH -> MutableStateFlow(UiState.Success(0.0)) // placeholder
        }
    }

    fun getCountByFilter(dateFilter: DateFilter): StateFlow<UiState<Int>> {
        return when (dateFilter) {
            DateFilter.TODAY -> todayTotalExpenseCount
            DateFilter.WEEK -> weeklyTotalExpenseCount
            DateFilter.MONTH -> MutableStateFlow(UiState.Success(0)) // placeholder
        }
    }

    fun updateExpense(expenseResponseDto: ExpenseResponseDto) {
        viewModelScope.launch {
            try {
                _updateExpenseState.value = UiState.Loading
                val result = expenseRepository.updateExpense(expenseResponseDto)
                if(result > 0)
                    _updateExpenseState.value = UiState.Success(expenseResponseDto)
                else
                    _updateExpenseState.value = UiState.Error("Internal Error !!")
            } catch (e: Exception) {
                _updateExpenseState.value = UiState.Error(e.toString())
            }

        }

    }

    fun deleteExpense(expenseResponseDto: ExpenseResponseDto) {
        viewModelScope.launch {
            try {
                _deleteExpenseState.value = UiState.Loading
                val result = expenseRepository.deleteExpense(expenseResponseDto)
                if(result > 0)
                    _deleteExpenseState.value = UiState.Success(expenseResponseDto)
                else
                    _deleteExpenseState.value = UiState.Error("Internal Error !!")
            } catch (e: Exception) {
                _deleteExpenseState.value = UiState.Error(e.toString())
            }

        }

    }

}