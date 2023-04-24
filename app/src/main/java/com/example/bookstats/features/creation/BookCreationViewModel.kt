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
        MutableStateFlow(BookCreationUiState.Init)
    val uiState: StateFlow<BookCreationUiState> = _uiState

    private val Any.logTag: String
        get() {
            val tag = javaClass.simpleName
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

    init {
        viewModelScope.launch {
            _uiState.emit(BookCreationUiState.Success("", "", 0))
        }
    }

    fun setBookName(bookName: String) {
        val state = _uiState.value as BookCreationUiState.Success
        with(state) {
            _uiState.value = copy(bookName = bookName, saveButtonEnabled = isSaveButtonEnabled())
        }
    }

    fun setBookAuthor(bookAuthor: String) {
        val state = _uiState.value as BookCreationUiState.Success
        with(state) {
            _uiState.value = copy(bookAuthor = bookAuthor, saveButtonEnabled = isSaveButtonEnabled())
        }
    }

    fun setNumberOfPages(numberOfPages: String) {
        val state = _uiState.value as BookCreationUiState.Success
        with(state) {
            _uiState.value = copy(numberOfPages = numberOfPages.toInt(), saveButtonEnabled = true)
        }
    }

    fun createBook() {
        viewModelScope.launch {
            with(_uiState.value as BookCreationUiState.Success) {
                if (bookAuthor.isNullOrBlank()) {
                    _uiState.emit(BookCreationUiState.Error(BookCreationUiState.Error.Reason.MissingAuthor))
                } else if (bookName.isNullOrBlank()) {
                    _uiState.emit(BookCreationUiState.Error(BookCreationUiState.Error.Reason.MissingBookName))
                } else if (0 >= numberOfPages) {
                    _uiState.emit(BookCreationUiState.Error(BookCreationUiState.Error.Reason.NoPages))
                } else {
                    try {
                        saveBook()
                    } catch (ex: java.lang.Exception) {
                        _uiState.emit(
                            BookCreationUiState.Error(
                                BookCreationUiState.Error.Reason.Unknown(
                                    ex
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun saveBook() {
        viewModelScope.launch {
            with(_uiState.value as BookCreationUiState.Success) {
                repository.addBookWithSessions(
                    BookWithSessions(
                        bookName!!, bookAuthor!!, numberOfPages, 0, mutableListOf()
                    )
                )
                Log.i(logTag, "Saved new book: $bookName")
            }
            _uiState.emit(BookCreationUiState.Done)
        }
    }

    private fun isSaveButtonEnabled(): Boolean {
        val state = _uiState.value as BookCreationUiState.Success
        if (state.bookName.isNullOrBlank()) {
            return false
        } else if (state.bookAuthor.isNullOrBlank()) {
            return false
        } else if (state.numberOfPages < 0) {
            return false
        }
        return true
    }

}