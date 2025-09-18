package me.rajesh.expensetracker.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import me.rajesh.expensetracker.app.MyApplication
import me.rajesh.expensetracker.data.db.AppDatabase
import me.rajesh.expensetracker.data.db.dao.ExpenseDao
import me.rajesh.expensetracker.data.db.service.DatabaseService
import me.rajesh.expensetracker.data.repository.localRepo.ExpenseRepository
import me.rajesh.expensetracker.di.module.ApplicationModule
import me.rajesh.expensetracker.di.module.DatabaseModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, DatabaseModule::class])
interface ApplicationComponent {

    fun inject(application: MyApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun funDatabaseService(): DatabaseService

    fun getExpenseRepository(): ExpenseRepository

}