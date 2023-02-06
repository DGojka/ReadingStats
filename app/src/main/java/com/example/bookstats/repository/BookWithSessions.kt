package com.example.bookstats.repository

data class BookWithSessions(
    val name: String,
    val author: String,
    val totalPages: Int,
    val currentPage: Int,
    val sessions: List<Session>
) {
    var id: Long = 0
}