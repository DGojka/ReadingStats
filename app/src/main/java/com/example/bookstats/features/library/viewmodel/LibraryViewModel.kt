package com.example.bookstats.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.library.viewmodel.uistate.LibraryUiState
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryViewModel @Inject constructor(
    private val repository: Repository,
    private val currentBookDb: CurrentBookDb
) : ViewModel() {
    private val _uiState = initUiState()
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
        }
    }

    fun initBookMoreDetails(id: Int, onInitialized: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            currentBookDb.updateCurrentBookId(id.toLong())
            withContext(Dispatchers.Main) {
                onInitialized()
            }
        }
    }

    private fun initUiState() = MutableStateFlow(LibraryUiState(true, mutableListOf(), null, 0))
}
