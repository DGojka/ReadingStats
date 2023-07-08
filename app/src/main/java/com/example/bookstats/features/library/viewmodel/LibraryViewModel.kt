package com.example.bookstats.features.library.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.library.viewmodel.uistate.LibraryUiState
import com.example.bookstats.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(LibraryUiState(true, mutableListOf(), null, 0, null, null))
    val uiState: StateFlow<LibraryUiState> = _uiState

    fun fetchBooksFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    bookList = repository.getBooksWithSessions(),
                    lastBook = repository.getLastBook(),
                    currentStreak = repository.getCurrentStreak()
                )
            Log.e("asd", repository.getBooksWithSessions().toString())
        }
    }

    fun moreDetails(id: Int, navigate: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(
                bookClicked = repository.getBookWithSessionsById(id = id.toLong())
            )
            withContext(Dispatchers.Main) {
                navigate()
            }
        }
    }

}