package me.rajesh.expensetracker.di.component

import dagger.Component
import me.rajesh.expensetracker.di.ReportFragmentScope
import me.rajesh.expensetracker.di.module.ReportFragmentModule
import me.rajesh.expensetracker.ui.fragments.report.ReportFragment

@ReportFragmentScope
@Component(dependencies = [ActivityComponent::class], modules = [ReportFragmentModule::class])
interface ReportFragmentComponent {
    fun inject(fragment: ReportFragment)
}