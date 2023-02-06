package com.example.bookstats.repository

import com.example.bookstats.database.AppDatabase
import com.example.bookstats.database.entity.BookEntity
import com.example.bookstats.database.entity.BookWithSessionsEntity
import com.example.bookstats.database.entity.SessionEntity

class RepositoryImpl(private val db: AppDatabase) : Repository {
    override suspend fun getBooksWithSessions(): List<BookWithSessions> {
        return db.bookWithSessionDao().getBooks().map {
            toBookWithSession(it)
        }
    }

    override suspend fun getBookWithSessionsById(id: Long): BookWithSessions {
        val bookWithSessions = db.bookWithSessionDao().getBooksById(id)
        return toBookWithSession(bookWithSessions)
    }

    override suspend fun addBookWithSessions(book: BookWithSessions) {
        val bookId = generateBookId()
        with(book) {
            db.bookDao().add(BookEntity(bookId, name, author, totalPages, currentPage))
        }
    }

    override suspend fun addSessionToTheBook(bookId: Long, session: Session) {
        val sId = generateSessionId()
        with(session) {
            db.sessionDao()
                .add(
                    SessionEntity(
                        sId,
                        bookId,
                        sessionTimeSeconds,
                        pagesRead,
                        sessionStartDate,
                        sessionEndDate
                    )
                )
        }
    }

    private fun toBookWithSession(bookWithSessions: BookWithSessionsEntity): BookWithSessions {
        if (bookWithSessions.sessions != null) {
            val sessionsMapped: List<Session> = bookWithSessions.sessions
                .map {
                    Session(
                        it.sessionTimeSeconds,
                        it.pagesRead,
                        it.sessionStartDate,
                        it.sessionEndDate
                    ).apply {
                        this.id = it.sessionId
                    }
                }
            with(bookWithSessions.book) {
                return BookWithSessions(
                    name,
                    bookAuthor,
                    totalPages,
                    currentPage,
                    sessionsMapped
                ).apply { id = bookId }
            }
        }
        with(bookWithSessions.book) {
            return BookWithSessions(
                name,
                bookAuthor,
                totalPages,
                currentPage,
                listOf()
            ).apply { id = bookId }
        }
    }

    private fun generateBookId(): Long =
        db.bookDao().getAll().maxOfOrNull { it.bookId + 1 } ?: 0

    private fun generateSessionId(): Long =
        db.sessionDao().getAll().maxOfOrNull { it.sessionId + 1 } ?: 0
}