package com.example.flightmobileapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "linkTable")
data class Link (@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "linkId") var id : Int,
                 @ColumnInfo(name = "link") var link: String)