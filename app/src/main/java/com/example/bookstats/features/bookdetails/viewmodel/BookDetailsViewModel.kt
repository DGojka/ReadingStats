package com.example.bookstats.features.bookdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.bookdetails.tabs.general.helpers.mapToGeneralBookInfo
import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.mapToSessionDetails
import com.example.bookstats.features.bookdetails.viewmodel.uistate.BookDetailsUiState
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.repository.BookWithSessions
import com.example.bookstats.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookDetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val currentBookDb: CurrentBookDb,
    private val sessionCalculator: SessionCalculator
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(BookDetailsUiState())
    val uiState: StateFlow<BookDetailsUiState> = _uiState

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            with(repository.getBookWithSessionsById(currentBookDb.getCurrentBookId())) {
                _uiState.value =
                    _uiState.value.copy(
                        book = this,
                        bookInfo = mapToGeneralBookInfo(sessionCalculator),
                        bookPercentage = getBookPercentage(this),
                        sessionDetails = sessions.mapToSessionDetails(sessionCalculator)
                    )
            }
        }
    }

    fun deleteBook(onDelete: () -> Unit) {
        with(_uiState.value) {
            if (book != null)
                viewModelScope.launch(Dispatchers.IO) {
                    repository.deleteBookWithSessions(bookId = book.id)
                    withContext(Dispatchers.Main) {
                        onDelete()
                    }
                }
        }
    }

    fun getCurrentBookId() = currentBookDb.getCurrentBookId()

    private fun getBookPercentage(book: BookWithSessions): Int {
        book.apply {
            return (((currentPage.toFloat() - startingPage) / (totalPages.toFloat() - startingPage)) * 100).toInt()
        }
    }
}
