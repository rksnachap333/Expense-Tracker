package me.rajesh.expensetracker.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationContext

@Qualifier
@Retention (AnnotationRetention.BINARY)
annotation class ActivityContext