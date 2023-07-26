package com.example.bookstats.repository

import android.util.Log
import com.example.bookstats.database.AppDatabase
import com.example.bookstats.database.entity.BookEntity
import com.example.bookstats.database.entity.BookSessionEntity
import com.example.bookstats.database.entity.BookWithSessionsEntity
import com.example.bookstats.database.entity.SessionEntity
import com.example.bookstats.network.ApiService
import com.example.bookstats.network.VolumeInfo
import java.time.LocalDateTime

class RepositoryImpl(private val db: AppDatabase, private val apiService: ApiService) : Repository {

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
            db.bookDao()
                .add(
                    BookEntity(
                        bookId,
                        name,
                        author,
                        totalPages,
                        currentPage,
                        bookImage = image,
                        filters = filters
                    )
                )
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
                    bookImage,
                    filters
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
        val date = LocalDateTime.now().toLocalDate().minusDays(1)
        var streak = 0
        var isFirstDayTaken = false
        var lastDate = date

        for (session in sessions.asReversed()) {
            if (!isFirstDayTaken && date.plusDays(1) == session.sessionStartDate.toLocalDate()) {
                streak++
                isFirstDayTaken = true
                continue
            }
            if (lastDate == session.sessionStartDate.toLocalDate()) {
                streak++
                lastDate = lastDate.minusDays(1)
            } else if (lastDate.plusDays(1) != session.sessionStartDate.toLocalDate()) {
                break
            }
        }
        return streak
    }

    override suspend fun getBookByISBN(isbn: String): VolumeInfo? {
        val response = apiService.getBookByISBN("isbn:$isbn")
        return if (response.isSuccessful) {
            val bookResponse = response.body()?.items?.get(0)
            bookResponse?.volumeInfo
        } else {
            Log.e("error", response.errorBody().toString())
            null
            //TODO: implement
        }
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
                filters,
                sessionsMapped
            ).apply { id = bookId }
        }
    }

    private fun generateBookId(): Long =
        db.bookDao().getAll().maxOfOrNull { it.bookId + 1 } ?: 0

    private fun generateSessionId(): Long =
        db.sessionDao().getAll().maxOfOrNull { it.sessionId + 1 } ?: 0
}