package com.example.bookstats.features.realtimesessions.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.realtimesessions.Timer
import com.example.bookstats.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealTimeSessionsViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(RealTimeSessionsUiState(0F))
    val uiState: StateFlow<RealTimeSessionsUiState> = _uiState
    private val timer: Timer = Timer()
    private var isPaused = true

    fun startSession(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = fetchBook(bookId) //remove it
            Log.e("asd", book.toString())
            timer.start()
            timer.flow.collect { currentMs ->
                with(_uiState.value) {
                    _uiState.value = copy(currentMs = currentMs)
                }
            }
        }
    }

    private suspend fun fetchBook(bookId: Long) = repository.getBookWithSessionsById(bookId)

}