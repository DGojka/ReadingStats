package com.example.bookstats.repository

import java.util.*

data class Session(
    val sessionTimeSeconds: Int,
    val pagesRead: Int,
    val sessionStartDate: Date,
    val sessionEndDate: Date
) {
    var id: Long = 0
}