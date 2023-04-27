package com.example.bookstats.app.di

import com.example.bookstats.features.creation.BookCreationFragment
import com.example.bookstats.features.library.ui.BookDetailsFragment
import com.example.bookstats.features.library.ui.LibraryFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(fragment: BookCreationFragment)

    fun inject(fragment: LibraryFragment)

    fun inject(fragment: BookDetailsFragment)
}