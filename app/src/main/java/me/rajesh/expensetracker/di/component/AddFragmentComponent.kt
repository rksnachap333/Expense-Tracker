package me.rajesh.expensetracker.di.component

import dagger.Component
import me.rajesh.expensetracker.di.AddFragmentScope
import me.rajesh.expensetracker.di.module.AddFragmentModule
import me.rajesh.expensetracker.ui.fragments.add.AddFragment

@AddFragmentScope
@Component(dependencies = [ActivityComponent::class], modules = [AddFragmentModule::class])
interface AddFragmentComponent {
    fun inject(fragment: AddFragment)
}