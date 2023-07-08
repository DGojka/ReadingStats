package com.example.bookstats.features.library.managers

import com.example.bookstats.repository.Session

class SessionCalculatorImpl : SessionCalculator {

    override fun getHourMinAndSec(currentMs: Float): String =
        (currentMs.toInt() / 1000).toHoursMinutesAndSec()

    override fun getAvgReadingTime(sessions: List<Session>): String =
        calculateAvgReadingTime(sessions).toHoursMinutesAndSec()

    override fun getTotalReadTime(sessions: List<Session>): String =
        calculateTotalReadingTimeSec(sessions).toHoursMinutesAndSec()

    override fun getAvgMinPerPage(sessions: List<Session>): String =
        DECIMAL_FORMAT.format(calculateMinPerPage(sessions))

    override fun getAvgPagesPerHour(sessions: List<Session>): String =
        DECIMAL_FORMAT.format(calculateAvgPagesPerHour(sessions))

    override fun calculatePagesReadInSession(newCurrentPage: Int, oldCurrentPage: Int): Int =
        newCurrentPage - oldCurrentPage

    override fun getPagesPerHourInSession(session: Session): String =
        DECIMAL_FORMAT.format(calculateAvgPagesPerHourInSession(session))

    override fun getMinPerPageInSession(session: Session): String =
        DECIMAL_FORMAT.format(calculateMinPerPageInSession(session))

    override fun isNewCurrentPageGreaterThanOld(newCurrentPage: Int, oldCurrentPage: Int): Boolean =
        newCurrentPage > oldCurrentPage

    override fun calculateSeconds(hours: Int, minutes: Int): Int {
        val totalMinutes = hours * 60 + minutes
        return totalMinutes * 60
    }

    override fun convertSecondsToMinutesAndSeconds(sessionTimeSeconds: Int): String =
        sessionTimeSeconds.toHoursMinutesAndSec()

    private fun calculateAvgPagesPerHour(sessions: List<Session>): Double {
        val totalTimeInH = calculateTotalReadingTimeSec(sessions) / HOUR_IN_SECONDS.toDouble()
        return calculateTotalPagesRead(sessions) / totalTimeInH
    }

    private fun calculateMinPerPage(sessions: List<Session>): Double {
        val totalTime =
            calculateTotalReadingTimeSec(sessions).toDouble() / MINUTE_IN_SECONDS.toDouble()
        return (totalTime / calculateTotalPagesRead(sessions))
    }

    private fun calculateAvgPagesPerHourInSession(session: Session): Double {
        with(session) {
            val totalTimeInH = sessionTimeSeconds.toDouble() / HOUR_IN_SECONDS.toDouble()
            return pagesRead.toDouble() / totalTimeInH
        }
    }

    private fun calculateMinPerPageInSession(session: Session): Double {
        with(session) {
            val totalTime = sessionTimeSeconds.toDouble() / MINUTE_IN_SECONDS.toDouble()
            return (totalTime / pagesRead.toDouble())
        }
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
        val hours = this / HOUR_IN_SECONDS
        val minutes = (this % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS
        val seconds = this % MINUTE_IN_SECONDS
        return when {
            hours > 0 -> {
                "$hours h ${minutes.toString().padStart(2, '0')} min"
            }
            minutes > 0 && seconds > 0 -> {
                "$minutes min ${seconds}s"
            }
            minutes > 0 -> {
                "$minutes min"
            }
            else -> {
                "$seconds s"
            }
        }
    }

    companion object {
        private const val MINUTE_IN_SECONDS = 60
        private const val HOUR_IN_SECONDS = 3600
        private const val DECIMAL_FORMAT = "%.2f"
    }
}