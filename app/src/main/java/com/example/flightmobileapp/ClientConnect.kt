package com.example.flightmobileapp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL


// http://10.0.2.2:5000

class ClientConnect(private var context: Context) : AppCompatActivity() {
    private lateinit var urlConnection: URL
    private lateinit var myConnection: HttpURLConnection
    private lateinit var api: ApiConnectServer

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
        createAPI()
        return true
    }



    fun createAPI() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val url = urlConnection.toString()
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        api = retrofit.create(ApiConnectServer::class.java)
    }


    fun getImage(image : ImageView) {
        val body = api.getScreenShoot().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() != 200) {
                    showError("Can't get image from server")
                    return
                }
                val I = response.body()?.byteStream()
                val B = BitmapFactory.decodeStream(I)
                runOnUiThread {
                    image.setImageBitmap(B)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showError("Can't get image from server")
            }
        })
    }

    fun showError(msg: String) {
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(context, msg, duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun getAPI(): ApiConnectServer {
        return api
    }


}

