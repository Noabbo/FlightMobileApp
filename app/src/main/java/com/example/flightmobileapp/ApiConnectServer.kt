package com.example.flightmobileapp

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiConnectServer {
    @GET("/screenshot")
    fun getScreenShoot(): Call<ResponseBody>

    @POST("/api/command")
    fun postCommand(@Body rb: RequestBody): Call<ResponseBody>

}