package com.example.bookstats.features.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookstats.repository.Repository
import javax.inject.Inject

class BookCreationViewModelFactory @Inject constructor(
    private val repository: Repository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BookCreationViewModel::class.java) ->
                BookCreationViewModel(repository) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}