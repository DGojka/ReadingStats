package com.example.bookstats.features.realtimesessions.helpers

import android.content.Context
import javax.inject.Inject

class CurrentBookDb @Inject constructor(val context: Context) {
    private val prefs = context.getSharedPreferences(CURRENT_BOOK, Context.MODE_PRIVATE)

    fun updateCurrentBookId(id: Long) {
        val editor = prefs.edit()
        editor.putLong(CURRENT_BOOK_ID, id)
        editor.apply()
    }

    fun getCurrentBookId() = prefs.getLong(CURRENT_BOOK_ID, 0)

    companion object {
        const val CURRENT_BOOK = "currentBook"
        const val CURRENT_BOOK_ID = "currentBookId"
    }
}