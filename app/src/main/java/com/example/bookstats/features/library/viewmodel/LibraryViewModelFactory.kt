package com.example.bookstats.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.repository.Repository
import javax.inject.Inject

class LibraryViewModelFactory @Inject constructor(
    private val repository: Repository,
    private val sessionCalculator: SessionCalculator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LibraryViewModel::class.java) ->
               LibraryViewModel(repository,sessionCalculator) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}