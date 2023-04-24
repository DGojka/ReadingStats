package com.example.bookstats.app

import android.app.Application
import com.example.bookstats.app.di.*


class ReadingStatsApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}