package com.example.bookstats.database.dao

import androidx.room.*
import com.example.bookstats.database.entity.BookSessionEntity
import com.example.bookstats.database.entity.BookWithSessionsEntity

@Dao
interface BookWithSessionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookSession: BookSessionEntity)

    @Transaction
    @Query("SELECT * FROM book")
    fun getBooks(): List<BookWithSessionsEntity>

    @Transaction
    @Query("SELECT * FROM book WHERE bookId LIKE :id ")
    fun getBookById(id: Long): BookWithSessionsEntity

    @Delete
    fun delete(bookSession: BookSessionEntity)

    @Update
    fun update(bookSession: BookSessionEntity)
}