package com.example.bookstats.repository

import com.example.bookstats.database.AppDatabase
import com.example.bookstats.database.entity.BookEntity
import com.example.bookstats.database.entity.BookSessionEntity
import com.example.bookstats.database.entity.BookWithSessionsEntity
import com.example.bookstats.database.entity.SessionEntity

class RepositoryImpl(private val db: AppDatabase) : Repository {
    override suspend fun getBooksWithSessions(): List<BookWithSessions> {
        return db.bookWithSessionDao().getBooks().map {
            mapBookWithSessionsEntity(it)
        }
    }

    override suspend fun getBookWithSessionsById(id: Long): BookWithSessions {
        val bookWithSessions = db.bookWithSessionDao().getBooksById(id)
        return mapBookWithSessionsEntity(bookWithSessions)
    }

    override suspend fun addBookWithSessions(book: BookWithSessions) {
        val bookId = generateBookId()
        with(book) {
            db.bookDao().add(BookEntity(bookId, name, author, totalPages, currentPage,""))
        }
    }

    override suspend fun addSessionToTheBook(bookId: Long, session: Session) {
        val sId = generateSessionId()
        with(db.bookWithSessionDao().getBooksById(bookId).book){
            db.bookDao().update(BookEntity(bookId,name, bookAuthor, totalPages, currentPage + session.pagesRead, photoPath))
        }
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

    override suspend fun deleteBookWithSessions(bookId: Long) {
        val bookWithSessions = db.bookWithSessionDao().getBooksById(bookId)
        bookWithSessions.sessions.forEach {
            db.sessionDao().delete(it)
            db.bookWithSessionDao().delete(BookSessionEntity(bookId,it.sessionId))
        }
        db.bookDao().delete(bookWithSessions.book)
    }

    private fun mapBookWithSessionsEntity(bookWithSessions: BookWithSessionsEntity): BookWithSessions {
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

    private fun generateBookId(): Long =
        db.bookDao().getAll().maxOfOrNull { it.bookId + 1 } ?: 0

    private fun generateSessionId(): Long =
        db.sessionDao().getAll().maxOfOrNull { it.sessionId + 1 } ?: 0
}