package com.example.bookstats.features.library.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.library.managers.SessionCalculator
import com.example.bookstats.features.library.managers.helpers.DialogDetails
import com.example.bookstats.features.library.tabs.sessions.SessionListItem
import com.example.bookstats.features.library.viewmodel.uistate.LibraryUiState
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LibraryViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionCalculator: SessionCalculator
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        fetchBooksFromDb()
    }

    fun fetchBooksFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value =
                _uiState.value.copy(isLoading = false, bookList = repository.getBooksWithSessions())
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

    fun getBookPercentage(): Int {
        with(_uiState.value) {
            if (bookClicked != null) {
                bookClicked.apply {
                    return ((currentPage.toFloat() / totalPages.toFloat()) * 100).toInt()
                }
            }
        }
        return 0
    }

    fun getAvgReadingTime(sessions: List<Session>): String =
        sessionCalculator.getAvgReadingTime(sessions)

    fun getTotalReadTime(sessions: List<Session>): String =
        sessionCalculator.getTotalReadTime(sessions)

    fun getAvgMinPerPage(sessions: List<Session>): String =
        sessionCalculator.getAvgMinPerPage(sessions)

    fun getAvgPagesPerHour(sessions: List<Session>): String =
        sessionCalculator.getAvgPagesPerHour(sessions)

    private suspend fun saveSessionByDialog(dialogDetails: DialogDetails) {
        dialogDetails.apply {
            with(_uiState.value) {
                repository.addSessionToTheBook(
                    bookId = _uiState.value.bookClicked!!.id,
                    Session(
                        sessionTimeSeconds = sessionCalculator.calculateSeconds(
                            hoursRead,
                            minutesRead
                        ),
                        pagesRead = sessionCalculator.calculatePagesReadInSession(
                            dialogDetails.currentPage!!,
                            bookClicked?.currentPage!!
                        ),
                        sessionEndDate = readingSessionDate!!.atStartOfDay(),
                        sessionStartDate = readingSessionDate.atStartOfDay()
                    )
                )
            }
        }
    }

    private fun isAllFieldsFilled(): Boolean {
        with(_uiState.value.dialogDetails) {
            if (this != null) {
                return readingSessionDate != null && currentPage != null && sessionCalculator.calculateSeconds(
                    hoursRead,
                    minutesRead
                ) > 0 && sessionCalculator.isNewCurrentPageGreaterThanOld(
                    newCurrentPage = currentPage,
                    oldCurrentPage = _uiState.value.bookClicked!!.currentPage
                )
            }
            return false
        }
    }

    fun mapSessionsToSessionListItem(sessions: List<Session>): List<SessionListItem> =
        sessions.map {
            with(it) {
                SessionListItem(
                    date = sessionStartDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    pagesRead.toString(),
                    sessionCalculator.convertSecondsToMinutesAndSeconds(sessionTimeSeconds),
                    sessionCalculator.getAvgMinPerPage(sessions),
                    sessionCalculator.getAvgPagesPerHour(sessions)
                )
            }
        }

}