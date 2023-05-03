package com.example.bookstats.features.library.managers

import com.example.bookstats.repository.Session

interface SessionCalculator {
    fun getAvgReadingTime(sessions: List<Session>): String

    fun getTotalReadTime(sessions: List<Session>): String

    fun getAvgMinPerPage(sessions: List<Session>): String

    fun getAvgPagesPerHour(sessions: List<Session>): String

    fun calculateSeconds(hours: Int, minutes: Int): Int

    fun convertSecondsToMinutesAndSeconds(sessionTimeSeconds: Int): String

    fun calculatePagesReadInSession(newCurrentPage: Int, oldCurrentPage: Int): Int

    fun isNewCurrentPageGreaterThanOld(newCurrentPage: Int, oldCurrentPage: Int) : Boolean
}