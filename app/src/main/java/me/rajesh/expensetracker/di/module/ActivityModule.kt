package me.rajesh.expensetracker.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import me.rajesh.expensetracker.data.repository.localRepo.ExpenseRepository
import me.rajesh.expensetracker.di.ActivityContext
import me.rajesh.expensetracker.ui.base.ViewModelProviderFactory
import me.rajesh.expensetracker.ui.fragments.common.shared_viewmodel.ExpenseViewModel
import me.rajesh.expensetracker.ui.fragments.common.viewholder.TransactionAdapter


@Module
class ActivityModule(
    private val activity: AppCompatActivity
) {
    @ActivityContext
    @Provides
    fun provideContext(): Context {
        return activity
    }

    @ActivityContext
    @Provides
    fun provideExpenseViewModel(
        expenseRepository: ExpenseRepository
    ): ExpenseViewModel {
        return ViewModelProvider(
            activity,
            ViewModelProviderFactory(ExpenseViewModel::class) {
                ExpenseViewModel(expenseRepository)
            }
        )[ExpenseViewModel::class.java]
    }

    @Provides
    fun provideTransactionAdapter(): TransactionAdapter {
        return TransactionAdapter(arrayListOf())
    }

}