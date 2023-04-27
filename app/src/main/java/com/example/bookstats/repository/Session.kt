package com.example.bookstats.repository

import java.time.LocalDateTime

data class Session(
    val sessionTimeSeconds: Int,
    val pagesRead: Int,
    val sessionStartDate: LocalDateTime,
    val sessionEndDate: LocalDateTime
) {
    var id: Long = 0
}