package com.example.bookstats.features.realtimesessions.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun startSession(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = fetchBook(bookId) //remove it
            Log.e("asd", book.toString())
            //TODO: implement timer
        }
    }

    private suspend fun fetchBook(bookId: Long) = repository.getBookWithSessionsById(bookId)

}