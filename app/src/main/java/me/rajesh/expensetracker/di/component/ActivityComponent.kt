package me.rajesh.expensetracker.di.component

import android.content.Context
import dagger.Component
import me.rajesh.expensetracker.MainActivity
import me.rajesh.expensetracker.di.ActivityContext
import me.rajesh.expensetracker.di.ActivityScope
import me.rajesh.expensetracker.di.module.ActivityModule
import me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel.ExpenseViewModel
import me.rajesh.expensetracker.ui.fragments.common.viewholder.TransactionAdapter

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(activity: MainActivity)

    @ActivityContext
    fun getActivityContext(): Context

    fun provideExpenseViewModel(): ExpenseViewModel

    fun provideTransactionAdapter() : TransactionAdapter

}