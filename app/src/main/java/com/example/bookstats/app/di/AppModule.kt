package com.example.bookstats.app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.bookstats.database.AppDatabase
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.bookdetails.managers.SessionCalculatorImpl
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.features.realtimesessions.helpers.ElapsedTimeDb
import com.example.bookstats.features.realtimesessions.timer.Timer
import com.example.bookstats.network.ApiService
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Singleton
    @Provides
    fun provideAppDatabase(): AppDatabase {
        return Room.databaseBuilder(
            app.applicationContext,
            AppDatabase::class.java,
            AppDatabase.NAME
        ).addMigrations(AppDatabase.MIGRATION_5_6, AppDatabase.MIGRATION_6_7).build()
    }

    @Singleton
    @Provides
    fun provideRepository(database: AppDatabase, apiService: ApiService): Repository {
        return RepositoryImpl(database, apiService)
    }

    @Singleton
    @Provides
    fun provideSessionCalculator(): SessionCalculator {
        return SessionCalculatorImpl()
    }

    @Singleton
    @Provides
    fun provideAppContext(): Context {
        return app.applicationContext
    }

    @Singleton
    @Provides
    fun provideTimerServiceHelper(context: Context): TimerServiceHelper {
        return TimerServiceHelper(context)
    }

    @Singleton
    @Provides
    fun provideCurrentBookDb(context: Context): CurrentBookDb {
        return CurrentBookDb(context)
    }

    @Singleton
    @Provides
    fun provideTimer(): Timer {
        return Timer()
    }

    @Singleton
    @Provides
    fun provideElapsedTimeDb(context: Context): ElapsedTimeDb {
        return ElapsedTimeDb(context = context)
    }
}