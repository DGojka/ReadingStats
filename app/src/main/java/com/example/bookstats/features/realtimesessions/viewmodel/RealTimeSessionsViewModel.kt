package com.example.bookstats.features.realtimesessions.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.bookdetails.tabs.sessions.SessionListItem
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.features.realtimesessions.timer.TimerService.Companion.CURRENT_MS
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RealTimeSessionsViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionCalculator: SessionCalculator,
    private val bookDb: CurrentBookDb,
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
            val currentMs = intent?.getFloatExtra(CURRENT_MS, 0f) ?: 0F
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

    fun endSessionWithoutSaving() {
        timerServiceHelper.unregisterTimerUpdateReceiver(timerUpdateReceiver)
        timerServiceHelper.stopService()
    }

    fun saveSession(newCurrentPage: Int, showSummary: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookWithSessionsById(bookDb.getCurrentBookId())
            if (sessionCalculator.isNewCurrentPageGreaterThanOld(
                    newCurrentPage = newCurrentPage,
                    oldCurrentPage = book.currentPage
                )
                && book.totalPages >= newCurrentPage
            ) {
                timerServiceHelper.stopService()
                _uiState.value = _uiState.value.copy(
                    session = Session(
                        sessionTimeSeconds = (uiState.value.currentMs / 1000).toInt(),
                        pagesRead = sessionCalculator.calculatePagesReadInSession(
                            newCurrentPage = newCurrentPage,
                            book.currentPage
                        ),
                        sessionStartDate = sessionStartDate,
                        sessionEndDate = sessionEndDate
                    )
                )
                if(_uiState.value.session!=null){
                    repository.addSessionToTheBook(
                        bookDb.getCurrentBookId(),
                        _uiState.value.session!!
                    )
                    withContext(Dispatchers.Main) {
                        showSummary()
                    }
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

    fun mapSessionsToSessionListItem(session: Session): SessionListItem =
        with(session) {
            SessionListItem(
                date = sessionStartDate.toLocalDate()
                    .format(DateTimeFormatter.ofPattern(BookDetailsViewModel.DATE_FORMAT)),
                pagesRead = pagesRead.toString(),
                readTime = sessionCalculator.convertSecondsToMinutesAndSeconds(
                    sessionTimeSeconds
                ),
                avgMinPerPage = sessionCalculator.getMinPerPageInSession(this),
                sessionCalculator.getPagesPerHourInSession(this)
            )
        }

}