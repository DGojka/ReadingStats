package com.example.bookstats.database.entity

import androidx.room.Entity

@Entity(
    tableName = "book_sessions",
    primaryKeys = ["bId", "sId"]
)
data class BookSessionEntity(
    val bId: Long,
    val sId: Long,
)