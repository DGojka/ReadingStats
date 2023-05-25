package com.example.bookstats.repository

import com.example.bookstats.network.VolumeInfo


interface Repository {
    suspend fun getBooksWithSessions(): List<BookWithSessions>

    suspend fun getBookWithSessionsById(id: Long): BookWithSessions

    suspend fun addBookWithSessions(book: BookWithSessions)

    suspend fun addSessionToTheBook(bookId: Long, session: Session)

    suspend fun deleteBookWithSessions(bookId: Long)

    suspend fun getLastBook(): BookWithSessions?

    suspend fun getCurrentStreak(): Int

    suspend fun getBookByISBN(isbn: String) : VolumeInfo?
}