package me.rajesh.expensetracker.di.component

import dagger.Component
import me.rajesh.expensetracker.di.ExpenseDetailFragmentScope
import me.rajesh.expensetracker.di.module.ExpenseDetailModule
import me.rajesh.expensetracker.ui.fragments.expense_detail.ExpenseDetails
import me.rajesh.expensetracker.ui.fragments.home.HomeFragment


@ExpenseDetailFragmentScope
@Component(dependencies = [ActivityComponent::class], modules = [ExpenseDetailModule::class])
interface ExpenseDetailComponent {
    fun inject(fragment: ExpenseDetails)
}