package com.example.bookstats.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey
    val sessionId: Long,
    val sessionTimeMs: Int,
    val pagesRead: Int,
    val sessionStartDate: Date,
    val sessionEndDate: Date
)
