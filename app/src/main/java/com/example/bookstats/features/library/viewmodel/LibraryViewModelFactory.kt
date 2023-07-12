package com.example.bookstats.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.repository.Repository
import javax.inject.Inject

class LibraryViewModelFactory @Inject constructor(
    private val repository: Repository,
    private val bookDb: CurrentBookDb
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LibraryViewModel::class.java) ->
                LibraryViewModel(repository, bookDb) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}