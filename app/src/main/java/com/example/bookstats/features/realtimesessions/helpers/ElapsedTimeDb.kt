package com.example.bookstats.features.realtimesessions.helpers

import android.content.Context
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ElapsedTimeDb @Inject constructor(val context: Context) {
    private val prefs = context.getSharedPreferences(ELAPSED_TIME, Context.MODE_PRIVATE)

    fun updateElapsedTime(seconds: Float) {
        val editor = prefs.edit()
        editor.putFloat(ELAPSED_TIME, seconds)
        editor.apply()
    }

    fun getElapsedTime() = prefs.getFloat(ELAPSED_TIME, 0F)


    fun getLastPauseTime(): LocalDateTime? {
        val savedValue = prefs.getString(LAST_PAUSE, null)

        return savedValue?.toLocalDateTime()
    }

    fun saveLastPause(date: LocalDateTime?) {
        val editor = prefs.edit()
        editor.putString(LAST_PAUSE, date.toStringOrNull())
        editor.apply()
    }


    private fun String?.toLocalDateTime(): LocalDateTime? {
        if (this == null) return null
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return LocalDateTime.parse(this, formatter)
    }

    private fun LocalDateTime?.toStringOrNull(): String? {
        if (this == null) return null
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return this.format(formatter)
    }

    companion object {
        private const val ELAPSED_TIME = "ELAPSED_TIME"
        private const val LAST_PAUSE = "LAST_PAUSE"
    }
}