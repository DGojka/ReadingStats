package com.example.bookstats.features.realtimesessions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class RealTimeSessionsViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(RealTimeSessionsUiState(0F))
    val uiState: StateFlow<RealTimeSessionsUiState> = _uiState
    private val timer: Timer = Timer()
    private lateinit var sessionStartDate: LocalDateTime
    private lateinit var sessionEndDate: LocalDateTime

    fun startSession() {
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
    }

    fun stopSession() {
        pauseTimer()
        sessionEndDate = LocalDateTime.now()
    }

    fun saveSession(bookId: Long, pagesRead: String, navigate: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSessionToTheBook(
                bookId,
                Session(
                    sessionTimeSeconds = timer.flow.value.toInt() / 1000,
                    pagesRead = pagesRead.toInt(),
                    sessionStartDate = sessionStartDate,
                    sessionEndDate = sessionEndDate
                )
            )
            withContext(Dispatchers.Main){
                navigate()
            }
        }
    }

}