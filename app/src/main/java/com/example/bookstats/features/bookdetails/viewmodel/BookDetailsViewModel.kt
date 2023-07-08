package com.example.bookstats.features.bookdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.bookdetails.managers.helpers.DialogDetails
import com.example.bookstats.features.bookdetails.tabs.sessions.SessionListItem
import com.example.bookstats.features.bookdetails.viewmodel.uistate.BookDetailsUiState
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
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

class BookDetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val currentBookDb: CurrentBookDb,
    private val sessionCalculator: SessionCalculator
) :
    ViewModel() {

    private val _uiState =
        MutableStateFlow(BookDetailsUiState(null, null))
    val uiState: StateFlow<BookDetailsUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value =
                _uiState.value.copy(book = repository.getBookWithSessionsById(currentBookDb.getCurrentBookId()))
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

    fun submitDialog() {
        if (isAllFieldsFilled()) {
            with(_uiState.value) {
                viewModelScope.launch(Dispatchers.IO) {
                    if (book != null && dialogDetails != null) {
                        saveSessionByDialog(dialogDetails)
                    }
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

    fun getBookPercentage(): Int {
        with(_uiState.value) {
            if (book != null) {
                book.apply {
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

    fun mapSessionsToSessionListItem(sessions: List<Session>): List<SessionListItem> =
        sessions.map {
            with(it) {
                SessionListItem(
                    date = sessionStartDate.toLocalDate()
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                    pagesRead = pagesRead.toString(),
                    readTime = sessionCalculator.convertSecondsToMinutesAndSeconds(
                        sessionTimeSeconds
                    ),
                    avgMinPerPage = sessionCalculator.getMinPerPageInSession(it),
                    sessionCalculator.getPagesPerHourInSession(it)
                )
            }
        }

    fun refreshBookClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            with(_uiState.value) {
                if (book != null) {
                    _uiState.value = copy(
                        book = repository.getBookWithSessionsById(id = book.id)
                    )
                }
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
                    oldCurrentPage = _uiState.value.book!!.currentPage
                )
            }
            return false
        }
    }

    private suspend fun saveSessionByDialog(dialogDetails: DialogDetails) {
        dialogDetails.apply {
            with(_uiState.value) {
                repository.addSessionToTheBook(
                    bookId = _uiState.value.book!!.id,
                    Session(
                        sessionTimeSeconds = sessionCalculator.calculateSeconds(
                            hoursRead,
                            minutesRead
                        ),
                        pagesRead = sessionCalculator.calculatePagesReadInSession(
                            dialogDetails.currentPage!!,
                            book?.currentPage!!
                        ),
                        sessionEndDate = readingSessionDate!!.atStartOfDay(),
                        sessionStartDate = readingSessionDate.atStartOfDay()
                    )
                )
            }
        }
    }

    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }
}