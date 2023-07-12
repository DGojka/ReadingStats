package com.example.bookstats.features.bookdetails.managers.helpers

import java.time.LocalDate

data class DialogDetails(
    val readingSessionDate: LocalDate? = null,
    val currentPage: Int? = null,
    val hoursRead: Int = 0,
    val minutesRead: Int = 0
)