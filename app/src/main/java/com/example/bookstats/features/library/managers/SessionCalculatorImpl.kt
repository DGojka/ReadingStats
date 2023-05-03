package com.example.bookstats.features.library.managers

import com.example.bookstats.repository.Session

class SessionCalculatorImpl : SessionCalculator {

    override fun getAvgReadingTime(sessions: List<Session>): String =
        calculateAvgReadingTime(sessions).toHoursMinutesAndSec()

    override fun getTotalReadTime(sessions: List<Session>): String =
        calculateTotalReadingTimeSec(sessions).toHoursMinutesAndSec()

    override fun getAvgMinPerPage(sessions: List<Session>): String =
        String.format("%.2f", calculateMinPerPage(sessions))

    override fun getAvgPagesPerHour(sessions: List<Session>): String =
        String.format("%.2f", calculateAvgPagesPerHour(sessions))

    override fun calculatePagesReadInSession(newCurrentPage: Int, oldCurrentPage: Int): Int =
        newCurrentPage - oldCurrentPage

    override fun isNewCurrentPageGreaterThanOld(newCurrentPage: Int, oldCurrentPage: Int): Boolean =
        newCurrentPage > oldCurrentPage

    override fun calculateSeconds(hours: Int, minutes: Int): Int {
        val totalMinutes = hours * 60 + minutes
        return totalMinutes * 60
    }

    override fun convertSecondsToMinutesAndSeconds(sessionTimeSeconds: Int): String =
        sessionTimeSeconds.toHoursMinutesAndSec()

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

    private fun Int.toHoursMinutesAndSec(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60
        return when {
            hours > 0 -> {
                "$hours h ${minutes.toString().padStart(2, '0')} min"
            }
            minutes > 0 -> {
                "$minutes min"
            }
            else -> {
                "$seconds s"
            }
        }
    }
}