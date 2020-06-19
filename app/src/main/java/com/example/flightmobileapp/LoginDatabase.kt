package com.example.flightmobileapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Link::class], version = 1, exportSchema = false)
abstract class LoginDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var lDao = database.linkDao()

                    // Delete all content here.
                    lDao.deleteAll()

                    // Add sample words.
                    var link = Link(System.currentTimeMillis(),"Hello")
                    lDao.insert(link)
                    link = Link(System.currentTimeMillis(),"World!")
                    lDao.insert(link)

                    // TODO: Add your own words!
                    link = Link(System.currentTimeMillis(),"TODO!")
                    lDao.insert(link)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LoginDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LoginDatabase::class.java,
                    "loginDatabase"
                )
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}