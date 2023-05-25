package com.example.bookstats.app.di

import android.app.Application
import androidx.room.Room
import com.example.bookstats.database.AppDatabase
import com.example.bookstats.features.library.managers.SessionCalculator
import com.example.bookstats.features.library.managers.SessionCalculatorImpl
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
        ).fallbackToDestructiveMigration().build()
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
}