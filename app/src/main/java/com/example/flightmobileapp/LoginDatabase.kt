package com.example.flightmobileapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Link::class], version = 1, exportSchema = false)
abstract class LoginDatabase : RoomDatabase() {
    abstract val linkDao : LinkDao

    companion object {
        @Volatile
        private var INSTANCE: LoginDatabase? = null

        fun getInstance(context: Context): LoginDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            synchronized(this) {
                var instance = INSTANCE
                if(instance==null) {
                    instance = Room.databaseBuilder (
                        context.applicationContext,
                        LoginDatabase::class.java,
                        "loginDatabase"
                    ).allowMainThreadQueries()
                        .build()
                }
                return instance
            }
        }
    }
}