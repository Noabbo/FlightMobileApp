package com.example.flightmobileapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "linkTable")
data class Link (@PrimaryKey @ColumnInfo(name = "time") val time: Long,
                 @ColumnInfo(name = "link") val link: String)