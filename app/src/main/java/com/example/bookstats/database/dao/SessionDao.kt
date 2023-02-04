package com.example.bookstats.database.dao

import androidx.room.*
import com.example.bookstats.database.entity.SessionEntity

@Dao
interface SessionDao {
    @Query("SELECT * FROM session")
    fun getAll(): List<SessionEntity>

    @Insert
    fun add(sessionEntity: SessionEntity)

    @Delete
    fun delete(sessionEntity: SessionEntity)

    @Update
    fun update(sessionEntity: SessionEntity)
}