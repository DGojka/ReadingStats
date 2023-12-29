package com.example.bookstats.features.realtimesessions.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.features.realtimesessions.helpers.ElapsedTimeDb
import com.example.bookstats.features.realtimesessions.timer.TimerService.Companion.CURRENT_MS
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class RealTimeSessionsViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionCalculator: SessionCalculator,
    private val bookDb: CurrentBookDb,
    private val elapsedTimeDb: ElapsedTimeDb,
    private val timerServiceHelper: TimerServiceHelper
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(RealTimeSessionsUiState("", null))
    val uiState: StateFlow<RealTimeSessionsUiState> = _uiState
    private lateinit var sessionStartDate: LocalDateTime
    private lateinit var sessionEndDate: LocalDateTime
    private var isPaused = true

    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val currentMs = intent?.getFloatExtra(CURRENT_MS, 0f) ?: 0F
            elapsedTimeDb.updateElapsedTime(currentMs / SECOND_IN_MS)
            _uiState.value = _uiState.value.copy(currentTime = currentMs.msToTimerText())
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerServiceHelper.unregisterTimerUpdateReceiver(timerUpdateReceiver)
    }

    fun startSession() {
        timerServiceHelper.registerTimerUpdateReceiver(timerUpdateReceiver)
        isPaused = false
        sessionStartDate = LocalDateTime.now()
    }

    fun pauseTimer() {
        timerServiceHelper.pause()
        elapsedTimeDb.saveLastPause(LocalDateTime.now())
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
        stopSession()
        resetTimer()
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
                val session = Session(
                    sessionTimeSeconds = (uiState.value.currentTime.timerTextToMs()
                        .msToSecond()).toInt(),
                    pagesRead = sessionCalculator.calculatePagesReadInSession(
                        newCurrentPage = newCurrentPage,
                        book.currentPage
                    ),
                    sessionStartDate = sessionStartDate,
                    sessionEndDate = sessionEndDate,
                )
                _uiState.value = _uiState.value.copy(
                    sessionDetails = sessionCalculator.calculateSessionDetails(session)
                )
                repository.addSessionToTheBook(
                    bookDb.getCurrentBookId(),
                    session
                )
                withContext(Dispatchers.Main) {
                    showSummary()
                }
                resetTimer()
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

    fun resumeTimerState() {
        val currentTime = LocalDateTime.now()
        val lastPause = elapsedTimeDb.getLastPauseTime()

        if (lastPause != null) {
            try {
                val timeFromLastPauseInSeconds = Duration.between(lastPause, currentTime)
                var timeElapsedSeconds = elapsedTimeDb.getElapsedTime()
                timeElapsedSeconds += timeFromLastPauseInSeconds.seconds
                timerServiceHelper.setTime(timeElapsedSeconds)
                setCurrentMs(timeElapsedSeconds * SECOND_IN_MS)
                resumeTimer()
            } catch (e: UninitializedPropertyAccessException) {
                elapsedTimeDb.saveLastPause(null)
            }
        }
    }

    fun isSessionEnded(): Boolean {
        return ::sessionEndDate.isInitialized
    }

    private fun setCurrentMs(currentMs: Float?) {
        currentMs?.let {
            _uiState.value = _uiState.value.copy(
                currentTime = it.msToTimerText()
            )
        }
    }

    private fun Float.msToTimerText(): String {
        val totalSeconds = (this.msToSecond()).toInt()
        val hours = totalSeconds / HOUR_IN_SECONDS
        val minutes = (totalSeconds % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS
        val seconds = totalSeconds % MINUTE_IN_SECONDS

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun String.timerTextToMs(): Float {
        val (hours, minutes, seconds) = this.split(":").map { it.toIntOrNull() ?: 0 }
        return ((hours * HOUR_IN_SECONDS + minutes * MINUTE_IN_SECONDS + seconds) * SECOND_IN_MS).toFloat()
    }

    private fun resetTimer() {
        timerServiceHelper.setTime(0F)
        elapsedTimeDb.saveLastPause(null)
        elapsedTimeDb.updateElapsedTime(0F)
    }

    private fun Float.msToSecond() = this / SECOND_IN_MS

    companion object {
        private const val HOUR_IN_SECONDS = 3600
        private const val MINUTE_IN_SECONDS = 60
        private const val SECOND_IN_MS = 1000
    }
}