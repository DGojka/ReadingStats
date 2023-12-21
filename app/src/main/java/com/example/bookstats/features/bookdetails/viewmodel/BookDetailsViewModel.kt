package com.example.bookstats.features.bookdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.bookdetails.managers.helpers.DialogDetails
import com.example.bookstats.features.bookdetails.tabs.general.helpers.mapToGeneralBookInfo
import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.mapToSessionDetails
import com.example.bookstats.features.bookdetails.viewmodel.uistate.BookDetailsUiState
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.repository.BookWithSessions
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class BookDetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val currentBookDb: CurrentBookDb,
    private val sessionCalculator: SessionCalculator
) :
    ViewModel() {

    private val _uiState =
        MutableStateFlow(BookDetailsUiState(null, null, null, null))
    val uiState: StateFlow<BookDetailsUiState> = _uiState

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookWithSessionsById(currentBookDb.getCurrentBookId())
            with(book) {
                _uiState.value =
                    _uiState.value.copy(
                        book = this,
                        bookInfo = mapToGeneralBookInfo(sessionCalculator),
                        bookPercentage = getBookPercentage(book),
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

    private fun getBookPercentage(book: BookWithSessions): Int {
        book.apply {
            return (((currentPage.toFloat() - startingPage) / (totalPages.toFloat() - startingPage)) * 100).toInt()
        }
    }

    private fun isAllFieldsFilled(): Boolean {
        with(_uiState.value.dialogDetails!!) {
            return isReadingSessionDateValid(readingSessionDate) && isCurrentPageValid(currentPage) && isReadingTimeValid(
                this
            )
        }
    }

    private fun isReadingSessionDateValid(date: LocalDate?): Boolean = date != null

    private fun isCurrentPageValid(currentPage: Int?): Boolean {
        return currentPage != null && sessionCalculator.isNewCurrentPageGreaterThanOld(
            newCurrentPage = currentPage,
            oldCurrentPage = _uiState.value.book?.currentPage!!
        )
    }

    private fun isReadingTimeValid(dialogDetails: DialogDetails?): Boolean =
        dialogDetails?.let { sessionCalculator.calculateSeconds(it.hoursRead, it.minutesRead) > 0 }
            ?: false

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

    fun getCurrentBookId() = currentBookDb.getCurrentBookId()

}