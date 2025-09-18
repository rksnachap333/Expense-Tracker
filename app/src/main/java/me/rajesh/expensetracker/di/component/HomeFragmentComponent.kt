package me.rajesh.expensetracker.di.component

import dagger.Component
import me.rajesh.expensetracker.di.HomeFragmentScope
import me.rajesh.expensetracker.di.module.HomeFragmentModule
import me.rajesh.expensetracker.ui.fragments.home.HomeFragment


@HomeFragmentScope
@Component(dependencies = [ActivityComponent::class], modules = [HomeFragmentModule::class])
interface HomeFragmentComponent {
    fun inject(fragment: HomeFragment)
}