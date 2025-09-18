package me.rajesh.expensetracker.di.component

import dagger.Component
import me.rajesh.expensetracker.di.ExpenseFragmentScope
import me.rajesh.expensetracker.di.module.ExpenseListModule
import me.rajesh.expensetracker.ui.fragments.expense_list.ExpenseListFragment

@ExpenseFragmentScope
@Component(dependencies = [ActivityComponent::class], modules = [ExpenseListModule::class])
interface ExpenseListComponent {
    fun inject(fragment: ExpenseListFragment)
}