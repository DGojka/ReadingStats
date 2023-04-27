package com.example.bookstats.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.library.managers.helpers.DialogDetails
import com.example.bookstats.features.library.viewmodel.uistate.LibraryUiState
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class LibraryViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        fetchBooksFromDb()
    }

    fun fetchBooksFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value =
                _uiState.value.copy(isLoading = false, bookList = repository.getBooksWithSessions())
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

    fun deleteBook(navigate: () -> Unit) {
        with(_uiState.value) {
            if (bookClicked != null)
                viewModelScope.launch(Dispatchers.IO) {
                    repository.deleteBookWithSessions(bookId = bookClicked.id)
                    _uiState.value =
                        copy(bookClicked = null, bookList = repository.getBooksWithSessions())
                    withContext(Dispatchers.Main) {
                        navigate()
                    }
                }
        }
    }

    fun setDialogDetails(
        readingSessionDate: LocalDate? = null,
        currentPage: Int? = null,
        hoursRead: Int? = null,
        minutesRead: Int? = null
    ) {
        with(_uiState.value) {
            val currentDialogDetails = _uiState.value.dialogDetails ?: DialogDetails()
            when {
                currentPage != null -> _uiState.value =
                    copy(dialogDetails = currentDialogDetails.copy(currentPage = currentPage))
                readingSessionDate != null -> _uiState.value =
                    copy(dialogDetails = currentDialogDetails.copy(readingSessionDate = readingSessionDate))
                hoursRead != null -> _uiState.value =
                    copy(dialogDetails = currentDialogDetails.copy(hoursRead = hoursRead))
                minutesRead != null -> _uiState.value =
                    copy(dialogDetails = currentDialogDetails.copy(minutesRead = minutesRead))
            }
        }
    }

    fun submitDialog() {
        if (isAllFieldsFilled()) {
            with(_uiState.value) {
                viewModelScope.launch(Dispatchers.IO) {
                    if (bookClicked != null && dialogDetails != null) {
                        saveSessionByDialog(dialogDetails)
                    }
                }
            }
        }
    }

    private suspend fun saveSessionByDialog(dialogDetails: DialogDetails) {
        dialogDetails.apply {
            repository.addSessionToTheBook(
                bookId = _uiState.value.bookClicked!!.id,
                Session(
                    sessionTimeSeconds = calculateSeconds(hoursRead, minutesRead),
                    pagesRead = calculatePagesRead(),
                    sessionEndDate = readingSessionDate!!.atStartOfDay(),
                    sessionStartDate = readingSessionDate.atStartOfDay()
                )
            )
        }
    }

    private fun calculateSeconds(hours: Int, minutes: Int): Int {
        val totalMinutes = hours * 60 + minutes
        return totalMinutes * 60
    }

    private fun calculatePagesRead(): Int =
        _uiState.value.dialogDetails!!.currentPage!! - _uiState.value.bookClicked?.currentPage!!

    private fun isAllFieldsFilled(): Boolean {
        with(_uiState.value.dialogDetails) {
            if (this != null) {
                return readingSessionDate != null && currentPage != null && calculateSeconds(
                    hoursRead,
                    minutesRead
                ) > 0 && isNewCurrentPageGreaterThanOld(currentPage)
            }
            return false
        }
    }

    private fun isNewCurrentPageGreaterThanOld(newCurrentPage: Int): Boolean {
        val oldPage = _uiState.value.bookClicked?.currentPage
        if (oldPage != null) {
            return newCurrentPage > oldPage
        }
        return false
    }

}