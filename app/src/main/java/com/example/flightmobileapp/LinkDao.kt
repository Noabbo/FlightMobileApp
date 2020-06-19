package com.example.flightmobileapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LinkDao {
    @Query("SELECT * from linkTable ORDER BY time DESC LIMIT 5")
    fun getRecentLinks(): List<Link>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(link: Link)

    @Query("DELETE FROM linkTable")
    suspend fun deleteAll()
}