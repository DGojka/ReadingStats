package com.example.bookstats.app.di

import android.app.Application
import androidx.room.Room
import com.example.bookstats.app.di.scope.ApplicationScope
import com.example.bookstats.database.AppDatabase
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.RepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @ApplicationScope
    @Provides
    fun provideRepository(appDatabase: AppDatabase): Repository {
        return RepositoryImpl(appDatabase)
    }

    @ApplicationScope
    @Provides
    fun provideDataBase(application: Application): AppDatabase = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, AppDatabase.NAME
    ).allowMainThreadQueries().build()
}