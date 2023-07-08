package com.example.bookstats.features.realtimesessions.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class RealTimeSessionsViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionCalculator: SessionCalculator,
    private val timerServiceHelper: TimerServiceHelper
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(RealTimeSessionsUiState(0F, null))
    val uiState: StateFlow<RealTimeSessionsUiState> = _uiState
    private lateinit var sessionStartDate: LocalDateTime
    private lateinit var sessionEndDate: LocalDateTime
    private var isPaused = true

    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val currentMs = intent?.getFloatExtra("CURRENT_MS", 0f) ?: 0F
            _uiState.value = _uiState.value.copy(currentMs = currentMs)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerServiceHelper.unregisterTimerUpdateReceiver(timerUpdateReceiver)
    }

    fun startSession() {
        isPaused = false
        sessionStartDate = LocalDateTime.now()
        timerServiceHelper.registerTimerUpdateReceiver(timerUpdateReceiver)
    }

    fun pauseTimer() {
        timerServiceHelper.pause()
        isPaused = true
    }

    fun resumeTimer() {
        timerServiceHelper.resume()
        isPaused = false
    }

    fun stopSession() {
        pauseTimer()
        sessionEndDate = LocalDateTime.now()
    }

    fun endSessionWithoutSaving(){
        timerServiceHelper.unregisterTimerUpdateReceiver(timerUpdateReceiver)
        timerServiceHelper.stopService()
    }

    fun saveSession(bookId: Long, newCurrentPage: Int, navigate: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookWithSessionsById(bookId)
            if (sessionCalculator.isNewCurrentPageGreaterThanOld(
                    newCurrentPage = newCurrentPage,
                    oldCurrentPage = book.currentPage
                )
                && book.totalPages >= newCurrentPage
            ) {
                timerServiceHelper.stopService()
                repository.addSessionToTheBook(
                    bookId,
                    Session(
                        sessionTimeSeconds = (uiState.value.currentMs / 1000).toInt(),
                        pagesRead = sessionCalculator.calculatePagesReadInSession(
                            newCurrentPage = newCurrentPage,
                            book.currentPage
                        ),
                        sessionStartDate = sessionStartDate,
                        sessionEndDate = sessionEndDate
                    )
                )
                withContext(Dispatchers.Main) {
                    navigate()
                }
            } else {
                val errorReason = if (book.totalPages < newCurrentPage) {
                    Error.Reason.NewPageIsGreaterThanTotalBookPages
                } else {
                    Error.Reason.NewPageIsLowerThanOld(book.currentPage)
                }

                _uiState.value = _uiState.value.copy(
                    error = Error(errorReason)
                )
            }
        }
    }

    fun isTimerPaused(): Boolean = isPaused

    fun setCurrentMs(currentMs: Float?) {
        _uiState.value = _uiState.value.copy(
            currentMs = currentMs!!
        )
    }

}