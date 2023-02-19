package com.example.bookstats.app

import android.app.Application
import com.example.bookstats.app.di.AppComponent

class ReadingStatsApp : Application() {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponent.create(this)
    }

    fun getAppComponent(): AppComponent {
        return appComponent
    }
}