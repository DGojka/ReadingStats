package com.example.bookstats.repository

interface Repository {
    suspend fun getBooksWithSessions(): List<BookWithSessions>

    suspend fun getBookWithSessionsById(id: Long): BookWithSessions

    suspend fun addBookWithSessions(book: BookWithSessions)

    suspend fun addSessionToTheBook(bookId: Long, session: Session)
}