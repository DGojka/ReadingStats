package com.example.bookstats.features.bookdetails.managers

/*import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.viewModelScope
import com.example.bookstats.R
import com.example.bookstats.databinding.AddingSessionDialogBinding
import com.example.bookstats.features.bookdetails.managers.helpers.DialogDetails
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class SessionDialogManager(
    private val layoutInflater: LayoutInflater,
    private val viewModel: BookDetailsViewModel
) {
    private var dialogBinding = AddingSessionDialogBinding.inflate(layoutInflater)

    fun showAddSessionDialog() {
        dialogBinding = AddingSessionDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(layoutInflater.context)
            .setView(dialogBinding.root)
            .setTitle(R.string.add_session)

        initSubmitButton(dialogBuilder)
        initDateControls()
        initCurrentPage()
        initTimeControls()
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun initSubmitButton(dialogBuilder: AlertDialog.Builder) {
        dialogBuilder.setPositiveButton(R.string.add) { _, _ ->
            viewModel.submitDialog()
        }
    }

    private fun initDateControls() {
        with(dialogBinding) {
            sessionDateEditText.setOnClickListener {
                datePicker.visibility = View.VISIBLE
                hideTimeControls()
            }
            datePicker.init(
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            ) { view, year, monthOfYear, dayOfMonth ->
                val selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                dialogBinding.sessionDateEditText.setText(selectedDate.toString())
                viewModel.setDialogDetails(readingSessionDate = selectedDate)
                dialogBinding.datePicker.visibility = View.GONE
                showTimeControls()
            }
        }
    }

    private fun initCurrentPage() {
        dialogBinding.editTextCurrentPage.addTextChangedListener {
            if(!it.isNullOrBlank()) {
                viewModel.setDialogDetails(currentPage = it.toString().toInt())
            }
        }
    }

    private fun initTimeControls() {
        initTimeEditTexts()
        initNumberPickers()
    }

    private fun hideTimeControls() {
        with(dialogBinding) {
            editTextHour.visibility = View.GONE
            editTextMinutes.visibility = View.GONE
            textViewHours.visibility = View.GONE
            textViewMinutes.visibility = View.GONE
            textViewReadingTime.visibility = View.GONE
            hoursNumberPicker.visibility = View.GONE
            minutesNumberPicker.visibility = View.GONE
        }
    }

    private fun showTimeControls() {
        with(dialogBinding) {
            editTextHour.visibility = View.VISIBLE
            editTextMinutes.visibility = View.VISIBLE
            textViewHours.visibility = View.VISIBLE
            textViewMinutes.visibility = View.VISIBLE
            textViewReadingTime.visibility = View.VISIBLE
        }
    }

    private fun initTimeEditTexts() {
        with(dialogBinding) {
            editTextHour.apply {
                editTextHour.setText(HOURS_MIN_VALUE.toString())
                viewModel.setDialogDetails(hoursRead = 0)
                setOnClickListener {
                    visibility = View.INVISIBLE
                    hoursNumberPicker.visibility = View.VISIBLE
                }
            }
            editTextMinutes.apply {
                setText(MINUTES_MIN_VALUE.toString())
                viewModel.setDialogDetails(minutesRead = 0)
                setOnClickListener {
                    visibility = View.INVISIBLE
                    minutesNumberPicker.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initNumberPickers() {
        with(dialogBinding) {
            hoursNumberPicker.apply {
                var isScrolling: Boolean
                minValue = HOURS_MIN_VALUE
                maxValue = HOURS_MAX_VALUE
                value = HOURS_MIN_VALUE
                setOnScrollListener { view, scrollState ->
                    isScrolling = scrollState != NumberPicker.OnScrollListener.SCROLL_STATE_IDLE
                    if (!isScrolling) {
                        val hours = hoursNumberPicker.value
                        editTextHour.setText(hours.toString())
                        viewModel.setDialogDetails(hoursRead = hours)
                        visibility = View.GONE
                        editTextHour.visibility = View.VISIBLE
                    }
                }
            }
            minutesNumberPicker.apply {
                var isScrolling: Boolean
                minValue = MINUTES_MIN_VALUE
                maxValue = MINUTES_MAX_VALUE
                value = MINUTES_MIN_VALUE
                setOnScrollListener { view, scrollState ->
                    isScrolling = scrollState != NumberPicker.OnScrollListener.SCROLL_STATE_IDLE
                    if (!isScrolling) {
                        val minutes = minutesNumberPicker.value
                        editTextMinutes.setText(minutes.toString())
                        viewModel.setDialogDetails(minutesRead = minutes)
                        visibility = View.GONE
                        editTextMinutes.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    companion object {
        private const val HOURS_MIN_VALUE = 0
        private const val MINUTES_MIN_VALUE = 0
        private const val HOURS_MAX_VALUE = 23
        private const val MINUTES_MAX_VALUE = 59
    }
}*/
/*


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

fun submitDialog() {
    if (isAllFieldsFilled()) {
        with(_uiState.value) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
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
*/
