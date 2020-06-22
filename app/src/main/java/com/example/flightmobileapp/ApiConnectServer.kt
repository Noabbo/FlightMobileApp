package com.example.flightmobileapp

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET


interface ApiConnectServer {
    @GET("/screenshot")
    fun getScreenShoot(): Call<ResponseBody>

    //@POST("/api/command")
    //fun post(@Body rb:RequestBody): Call<ResponseBody>

}