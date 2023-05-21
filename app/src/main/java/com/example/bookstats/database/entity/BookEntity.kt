package com.example.bookstats.database.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey val bookId: Long,
    val name: String,
    val bookAuthor: String,
    val totalPages: Int,
    val currentPage: Int,
    var photoPath: String?,
    var bookImage: Bitmap
)