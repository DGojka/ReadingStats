package com.example.bookstats.features.creation

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookstats.activity.scope.ActivityScope
import com.example.bookstats.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class BookCreationViewModelModule {
    @Provides
    @ActivityScope
    fun provideBookCreationViewModel(
        activity: ComponentActivity,
        repository: Repository
    ): BookCreationViewModel {
        //return ViewModelProvider(activity.viewModelStor)
    }
}