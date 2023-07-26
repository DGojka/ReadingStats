package com.example.bookstats.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bookstats.database.converter.BitmapConverter
import com.example.bookstats.database.converter.DateConverter
import com.example.bookstats.database.converter.ListStringConverter
import com.example.bookstats.database.dao.BookDao
import com.example.bookstats.database.dao.BookWithSessionsDao
import com.example.bookstats.database.dao.SessionDao
import com.example.bookstats.database.entity.BookEntity
import com.example.bookstats.database.entity.BookSessionEntity
import com.example.bookstats.database.entity.SessionEntity

@Database(
    entities = [BookEntity::class, SessionEntity::class, BookSessionEntity::class],
    version = 6
)
@TypeConverters(DateConverter::class, BitmapConverter::class, ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    abstract fun sessionDao(): SessionDao

    abstract fun bookWithSessionDao(): BookWithSessionsDao

    companion object {
        const val NAME = "bookstatsdb"
        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE book ADD COLUMN filters TEXT NOT NULL DEFAULT '[]'")
            }
        }
    }
}