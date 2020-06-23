package com.example.flightmobileapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LinkDao {

    @Query(value = "SELECT * from linkTable ORDER BY linkId DESC")
    fun getAllLinks(): LiveData<List<Link>>

    @Query(value = "SELECT * from linkTable ORDER BY linkId DESC LIMIT 5")
    fun getRecentLinks(): LiveData<List<Link>>

    @Query(value = "SELECT EXISTS (SELECT 1 FROM linkTable WHERE link = :link)")
    fun isLinkInRoom(link: String) : Boolean

    @Query(value = "SELECT * FROM linkTable WHERE link LIKE :link LIMIT 1")
    fun findByName(link: String): Link

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(link: Link) : Long

    @Delete
    suspend fun deleteLink(link: Link) : Int

    @Query(value = "DELETE from linkTable")
    suspend fun deleteAll() : Int
}