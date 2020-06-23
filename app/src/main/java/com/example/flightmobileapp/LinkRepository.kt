package com.example.flightmobileapp

import android.app.Application
import androidx.lifecycle.LiveData


class LinkRepository(private val dao: LinkDao) {
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allLinks: LiveData<List<Link>> = dao.getAllLinks()
    val listLinks: LiveData<List<Link>> = dao.getRecentLinks()

    suspend fun insert(link: Link) : Long {
        return dao.insert(link)
    }

    suspend fun deleteLink(link: Link) : Int {
        return dao.deleteLink(link)
    }

    suspend fun deleteAll() : Int {
        return dao.deleteAll()
    }

    fun isLinkInRoom(link: String) : Boolean {
        return dao.isLinkInRoom(link)
    }

    fun findLink(link: String) : Link {
        return dao.findByName(link)
    }
}