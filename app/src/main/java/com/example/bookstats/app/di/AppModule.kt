package com.example.bookstats.app.di

import android.app.Application
import androidx.room.Room
import com.example.bookstats.database.AppDatabase
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
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun provideRepository(database: AppDatabase): Repository {
        return RepositoryImpl(database)
    }
}