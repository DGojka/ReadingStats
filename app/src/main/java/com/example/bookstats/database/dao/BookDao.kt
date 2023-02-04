package com.example.bookstats.database.dao

import androidx.room.*
import com.example.bookstats.database.entity.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    fun getAll(): List<BookEntity>

    @Insert
    fun add(bookEntity: BookEntity)

    @Delete
    fun delete(bookEntity: BookEntity)

    @Update
    fun update(bookEntity: BookEntity)
}