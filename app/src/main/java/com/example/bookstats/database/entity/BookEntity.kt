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
    val startingPage: Int,
    val currentPage: Int,
    val bookImage: Bitmap,
    val filters: List<String>
)