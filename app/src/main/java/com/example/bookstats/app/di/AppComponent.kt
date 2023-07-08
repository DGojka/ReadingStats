package com.example.bookstats.app.di

import com.example.bookstats.activity.MainActivity
import com.example.bookstats.features.creation.BookCreationFragment
import com.example.bookstats.features.library.ui.BookDetailsFragment
import com.example.bookstats.features.library.ui.LibraryFragment
import com.example.bookstats.features.realtimesessions.ui.RealTimeSessionFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(fragment: BookCreationFragment)

    fun inject(fragment: LibraryFragment)

    fun inject(fragment: BookDetailsFragment)

    fun inject(realTimeSessionFragment: RealTimeSessionFragment)

    fun inject(mainActivity: MainActivity)
}