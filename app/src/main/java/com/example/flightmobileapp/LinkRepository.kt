package com.example.flightmobileapp

import androidx.lifecycle.LiveData

class LinkRepository(private val linkDao: LinkDao) {
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allLinks: LiveData<List<Link>> = linkDao.getRecentLinks()

    suspend fun insert(link: Link) {
        linkDao.insert(link)
    }
}