package me.rajesh.expensetracker.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rajesh.expensetracker.di.ApplicationContext


@Module
class ApplicationModule {

    @ApplicationContext
    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application
    }

}