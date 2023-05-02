package com.example.bookstats.features.library.viewmodel

import android.util.Log
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
        calculateAvgReadingTime(sessions).toMinutesAndSec()

    fun getTotalReadTime(sessions: List<Session>): String =
        calculateTotalReadingTimeSec(sessions).toMinutesAndSec()

    fun getAvgMinPerPage(sessions: List<Session>): String =
        String.format("%.2f", calculateMinPerPage(sessions))


    fun getAvgPagesPerHour(sessions: List<Session>): String =
        String.format("%.2f", calculateAvgPagesPerHour(sessions))

    private fun calculateAvgPagesPerHour(sessions: List<Session>): Double {
        val totalTimeInH = calculateTotalReadingTimeSec(sessions) / 3600.0
        return calculateTotalPagesRead(sessions) / totalTimeInH
    }

    private fun calculateMinPerPage(sessions: List<Session>): Double {
        val totalTime = calculateTotalReadingTimeSec(sessions).toDouble() / 60.0
        return (totalTime / calculateTotalPagesRead(sessions))
    }

    private fun calculateTotalPagesRead(sessions: List<Session>): Int {
        return sessions.sumOf { it.pagesRead }
    }

    private fun calculateAvgReadingTime(sessions: List<Session>): Int {
        return if (sessions.isNotEmpty()) {
            calculateTotalReadingTimeSec(sessions) / sessions.size
        } else {
            0
        }
    }

    private fun calculateTotalReadingTimeSec(sessions: List<Session>): Int {
        var totalTime = 0
        sessions.forEach { totalTime += it.sessionTimeSeconds }
        return totalTime
    }

    private suspend fun saveSessionByDialog(dialogDetails: DialogDetails) {
        dialogDetails.apply {
            repository.addSessionToTheBook(
                bookId = _uiState.value.bookClicked!!.id,
                Session(
                    sessionTimeSeconds = calculateSeconds(hoursRead, minutesRead),
                    pagesRead = calculatePagesReadInSession(),
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

    private fun calculatePagesReadInSession(): Int =
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

    private fun Int.toMinutesAndSec(): String {
        val minutes = this / 60
        val seconds = this % 60
        return if (minutes > 0) {
            "${minutes}min ${seconds}s"
        } else {
            "${seconds}s"
        }
    }
}