package com.example.bookstats.features.creation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.repository.BookWithSessions
import com.example.bookstats.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookCreationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _uiState: MutableStateFlow<BookCreationUiState> =
        MutableStateFlow(BookCreationUiState())
    val uiState: StateFlow<BookCreationUiState> = _uiState

    private val Any.logTag: String
        get() {
            val tag = javaClass.simpleName
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

    fun setBookName(name: String) {
        val uiState = _uiState.value.copy(bookName = name)
        _uiState.value = uiState.copy(saveButtonEnabled = isSaveButtonEnabled(uiState))
    }

    fun setBookAuthor(author: String) {
        val uiState = _uiState.value.copy(bookAuthor = author)
        _uiState.value = uiState.copy(saveButtonEnabled = isSaveButtonEnabled(uiState))
    }

    fun setNumberOfPages(numberOfPages: String) {
        val uiState = _uiState.value.copy(
            numberOfPages = if (numberOfPages.isNotEmpty()) numberOfPages.toInt() else 0
        )
        _uiState.value = uiState.copy(saveButtonEnabled = isSaveButtonEnabled(uiState))
    }

    fun createBook() {
        viewModelScope.launch {
            with(_uiState.value) {
                if (bookAuthor.isBlank()) {
                    _uiState.value = copy(error = Error(Error.Reason.MissingAuthor))
                } else if (bookName.isBlank()) {
                    _uiState.value = copy(error = Error(Error.Reason.MissingBookName))
                } else if (0 >= numberOfPages) {
                    _uiState.value = copy(error = Error(Error.Reason.NoPages))
                } else {
                    try {
                        saveBook()
                    } catch (ex: java.lang.Exception) {
                        _uiState.value = copy(error = Error(Error.Reason.Unknown(ex)))
                    }
                }
            }
        }
    }

    private fun saveBook() {
        viewModelScope.launch {
            with(_uiState.value) {
                repository.addBookWithSessions(
                    BookWithSessions(
                        bookName, bookAuthor, numberOfPages, 0, mutableListOf()
                    )
                )
                Log.i(logTag, "Saved new book: $bookName")
                _uiState.value = copy(bookCreated = true)
            }

        }
    }

    private fun isSaveButtonEnabled(state: BookCreationUiState): Boolean {
        with(state) {
            if (bookName.isBlank()) {
                return false
            } else if (bookAuthor.isBlank()) {
                return false
            } else if (numberOfPages <= 0) {
                return false
            }
            return true
        }
    }
}