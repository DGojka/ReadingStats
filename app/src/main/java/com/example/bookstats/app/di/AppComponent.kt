package com.example.bookstats.app.di

import android.app.Activity
import android.app.Application
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.app.di.scope.ApplicationScope
import com.example.bookstats.repository.Repository
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }

    fun inject(application: ReadingStatsApp)

    fun repository(): Repository

    companion object {
        fun create(application: ReadingStatsApp): AppComponent =
            DaggerAppComponent.factory().create(application).apply {
                inject(application)
            }

        val Activity.appComponent: AppComponent
            get() = (application as ReadingStatsApp).getAppComponent()
    }
}