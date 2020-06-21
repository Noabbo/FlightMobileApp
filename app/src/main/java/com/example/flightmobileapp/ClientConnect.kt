package com.example.flightmobileapp

import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class ClientConnect : AppCompatActivity() {
    private lateinit var urlConnection: URL
    private lateinit var myConnection: HttpURLConnection

    //http://localhost:5000/
    fun connect(url: String):Boolean {
        val tempUrl: URL
        try {
            tempUrl = URL(url)
            myConnection = tempUrl.openConnection() as HttpURLConnection

        } catch (e: Exception) {
            return false
        }
        urlConnection = tempUrl
        return true
    }


}