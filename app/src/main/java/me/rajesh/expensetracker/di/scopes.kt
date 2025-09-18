package me.rajesh.expensetracker.di

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class HomeFragmentScope

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class AddFragmentScope


@Scope
@Retention(AnnotationRetention.BINARY)
annotation class ExpenseFragmentScope

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class ReportFragmentScope

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class ExpenseDetailFragmentScope
