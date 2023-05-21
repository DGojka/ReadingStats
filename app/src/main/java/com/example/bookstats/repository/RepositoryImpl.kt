package com.example.bookstats.repository

import com.example.bookstats.database.AppDatabase
import com.example.bookstats.database.entity.BookEntity
import com.example.bookstats.database.entity.BookSessionEntity
import com.example.bookstats.database.entity.BookWithSessionsEntity
import com.example.bookstats.database.entity.SessionEntity
import java.time.LocalDate
import java.time.LocalDateTime

class RepositoryImpl(private val db: AppDatabase) : Repository {

    override suspend fun getBooksWithSessions(): List<BookWithSessions> {
        return db.bookWithSessionDao().getBooks().map {
            it.mapToBookWithSession()
        }
    }

    override suspend fun getBookWithSessionsById(id: Long): BookWithSessions =
        db.bookWithSessionDao().getBookById(id).mapToBookWithSession()

    override suspend fun addBookWithSessions(book: BookWithSessions) {
        val bookId = generateBookId()
        with(book) {
            db.bookDao().add(BookEntity(bookId, name, author, totalPages, currentPage, "", bookImage = image))
        }
    }

    override suspend fun addSessionToTheBook(bookId: Long, session: Session) {
        val sId = generateSessionId()
        with(db.bookWithSessionDao().getBookById(bookId).book) {
            db.bookDao().update(
                BookEntity(
                    bookId,
                    name,
                    bookAuthor,
                    totalPages,
                    currentPage + session.pagesRead,
                    photoPath,
                    bookImage
                )
            )
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
        val bookWithSessions = db.bookWithSessionDao().getBookById(bookId)
        bookWithSessions.sessions.forEach {
            db.sessionDao().delete(it)
            db.bookWithSessionDao().delete(BookSessionEntity(bookId, it.sessionId))
        }
        db.bookDao().delete(bookWithSessions.book)
    }

    override suspend fun getLastBook(): BookWithSessions? {
        val allSessions = db.sessionDao().getAll()
        val lastSession = if (allSessions.isNotEmpty()) allSessions.last() else return null
        return db.bookWithSessionDao().getBookById(lastSession.bookParentId).mapToBookWithSession()
    }

    override suspend fun getCurrentStreak(): Int {
        val sessions = db.sessionDao().getAll()
        if (sessions.isEmpty()) {
            return 0
        }

        val currentDate = LocalDateTime.now().toLocalDate()
        var isFirstDayTaken = false
        var streak = 0
        var lastDate: LocalDate? = null

        sessions.reversed().forEach { session ->
            val sessionDate = session.sessionStartDate.toLocalDate()

            if (sessionDate == currentDate.minusDays(1) && !isFirstDayTaken) {
                isFirstDayTaken = true
                streak++
                lastDate = sessionDate
            } else if (sessionDate == currentDate || (lastDate != null && sessionDate == lastDate!!.minusDays(
                    1
                ))
            ) {
                streak++
                lastDate = sessionDate
            } else {
                return@forEach
            }
        }
        return streak
    }

    private fun BookWithSessionsEntity.mapToBookWithSession(): BookWithSessions {
        val sessionsMapped: List<Session> = this.sessions
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
        with(this.book) {
            return BookWithSessions(
                name,
                bookAuthor,
                bookImage,
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