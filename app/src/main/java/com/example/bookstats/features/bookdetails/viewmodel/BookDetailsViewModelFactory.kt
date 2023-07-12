package com.example.bookstats.features.bookdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.repository.Repository
import javax.inject.Inject

class BookDetailsViewModelFactory @Inject constructor(
    private val repository: Repository,
    private val currentBookDb: CurrentBookDb,
    private val sessionCalculator: SessionCalculator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BookDetailsViewModel::class.java) ->
                BookDetailsViewModel(repository, currentBookDb, sessionCalculator) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}