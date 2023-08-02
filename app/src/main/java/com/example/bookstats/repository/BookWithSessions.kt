package com.example.bookstats.repository

import android.graphics.Bitmap

data class BookWithSessions(
    val name: String,
    val author: String,
    val image: Bitmap,
    val totalPages: Int,
    val startingPage: Int = 0,
    val currentPage: Int,
    val filters: List<String>,
    val sessions: List<Session>
) {
    var id: Long = 0
}