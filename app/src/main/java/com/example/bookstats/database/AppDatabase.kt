package com.example.bookstats.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bookstats.database.converter.BitmapConverter
import com.example.bookstats.database.converter.DateConverter
import com.example.bookstats.database.dao.BookDao
import com.example.bookstats.database.dao.BookWithSessionsDao
import com.example.bookstats.database.dao.SessionDao
import com.example.bookstats.database.entity.BookEntity
import com.example.bookstats.database.entity.BookSessionEntity
import com.example.bookstats.database.entity.SessionEntity

@Database(
    entities = [BookEntity::class, SessionEntity::class, BookSessionEntity::class],
    version = 4
)
@TypeConverters(DateConverter::class, BitmapConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    abstract fun sessionDao(): SessionDao

    abstract fun bookWithSessionDao(): BookWithSessionsDao

    companion object {
        const val NAME = "bookstatsdb"
    }
}