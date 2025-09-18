package me.rajesh.expensetracker.di.module

import androidx.appcompat.app.AppCompatActivity
import dagger.Module

@Module
class ExpenseDetailModule(
    private val activity: AppCompatActivity
) {
}