package com.example.bookstats.features.realtimesessions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.library.managers.SessionCalculator
import com.example.bookstats.features.realtimesessions.Timer
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
    private val sessionCalculator: SessionCalculator
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(RealTimeSessionsUiState(0F, null))
    val uiState: StateFlow<RealTimeSessionsUiState> = _uiState
    private val timer: Timer = Timer()
    private lateinit var sessionStartDate: LocalDateTime
    private lateinit var sessionEndDate: LocalDateTime
    private var isPaused = true

    fun startSession() {
        isPaused = false
        sessionStartDate = LocalDateTime.now()
        viewModelScope.launch(Dispatchers.IO) {
            timer.start()
            timer.flow.collect { currentMs ->
                with(_uiState.value) {
                    _uiState.value = copy(currentMs = currentMs)
                }
            }
        }
    }

    fun pauseTimer() {
        timer.pause()
        isPaused = true
    }

    fun resumeTimer() {
        timer.start()
        isPaused = false
    }

    fun stopSession() {
        pauseTimer()
        sessionEndDate = LocalDateTime.now()
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
                repository.addSessionToTheBook(
                    bookId,
                    Session(
                        sessionTimeSeconds = timer.flow.value.toInt() / 1000,
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
}