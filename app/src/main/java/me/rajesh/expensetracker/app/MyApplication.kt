package me.rajesh.expensetracker.app

import android.app.Application
import me.rajesh.expensetracker.data.repository.localRepo.ExpenseRepository
import me.rajesh.expensetracker.di.component.ApplicationComponent
import me.rajesh.expensetracker.di.component.DaggerApplicationComponent
import javax.inject.Inject

class MyApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var expenseRepository1: ExpenseRepository

    @Inject
    lateinit var expenseRepository2: ExpenseRepository


    override fun onCreate() {
        injectDependencies()
        super.onCreate()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .application(this)
            .build()

        applicationComponent.inject(this)

    }
}