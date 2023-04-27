package com.example.bookstats.features.library.managers

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import com.example.bookstats.R
import com.example.bookstats.databinding.AddingSessionDialogBinding
import com.example.bookstats.features.library.managers.helpers.DialogDetails
import java.time.LocalDate

class SessionDialogManager(private val layoutInflater: LayoutInflater) {
    private var dialogBinding = AddingSessionDialogBinding.inflate(layoutInflater)
    private var selectedDate: LocalDate? = null
    private val currentPage: Int? = null
    private var hours: Int = 0
    private var minutes: Int = 0

    fun showAddSessionDialog(onSubmit: (DialogDetails) -> Unit) {
        dialogBinding = AddingSessionDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(layoutInflater.context)
            .setView(dialogBinding.root)
            .setTitle(R.string.add_session)

        initSubmitButton(dialogBuilder,onSubmit)
        initDateControls()
        initTimeControls()

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun initSubmitButton(
        dialogBuilder: AlertDialog.Builder,
        onSubmit: (DialogDetails) -> Unit
    ) {
        dialogBuilder.setPositiveButton(R.string.add) { _, _ ->
            if (isAllFieldsFilled()) {
                onSubmit(
                    DialogDetails(
                        readingSessionDate = selectedDate!!,
                        currentPage = currentPage!!,
                        calculateSeconds()
                    )
                )
            }
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
                selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                dialogBinding.sessionDateEditText.setText(selectedDate.toString())
                dialogBinding.datePicker.visibility = View.GONE
                showTimeControls()
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
            textViewReadingTime.visibility= View.GONE
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
            textViewReadingTime.visibility= View.VISIBLE
        }
    }

    private fun initTimeEditTexts() {
        with(dialogBinding) {
            editTextHour.apply {
                setText(hours.toString())
                setOnClickListener {
                    visibility = View.INVISIBLE
                    hoursNumberPicker.visibility = View.VISIBLE
                }
            }
            editTextMinutes.apply {
                setText(minutes.toString())
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
                        hours = hoursNumberPicker.value
                        editTextHour.setText(hours.toString())
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
                        minutes = minutesNumberPicker.value
                        editTextMinutes.setText(minutes.toString())
                        visibility = View.GONE
                        editTextMinutes.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    private fun calculateSeconds(): Int {
        val totalMinutes = hours * 60 + minutes
        return totalMinutes * 60
    }

    private fun isAllFieldsFilled(): Boolean =
        selectedDate != null && currentPage != null && calculateSeconds() > 0

    companion object {
        private const val HOURS_MIN_VALUE = 0
        private const val MINUTES_MIN_VALUE = 0
        private const val HOURS_MAX_VALUE = 23
        private const val MINUTES_MAX_VALUE = 59
    }
}