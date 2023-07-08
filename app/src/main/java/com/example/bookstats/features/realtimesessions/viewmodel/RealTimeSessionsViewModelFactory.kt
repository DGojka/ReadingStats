package com.example.bookstats.features.realtimesessions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper
import com.example.bookstats.repository.Repository
import javax.inject.Inject

class RealTimeSessionsViewModelFactory @Inject constructor(
    private val repository: Repository,
    private val calculator: SessionCalculator,
    private val timerServiceHelper: TimerServiceHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RealTimeSessionsViewModel::class.java) ->
                RealTimeSessionsViewModel(repository, calculator, timerServiceHelper) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}