package com.example.bookstats.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithSessionsEntity(
    @Embedded
    val book: BookEntity,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookParentId",
    )
    val sessions: List<SessionEntity>
)