package com.example.flightmobileapp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.RequestBody
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

    // All Joystick parameters setters
    private var aileron: Double = 0.0
        set(value) {
            field = value
        }
    private var elevator: Double = 0.0
        set(value) {
            field = value
        }
    private var throttle: Double = 0.0
        set(value) {
            field = value
        }
    private var rudder: Double = 0.0
        set(value) {
            field = value
        }



    fun getAPI(): ApiConnectServer {
        return api
    }

    fun isValidHttp(url: String):Boolean {
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

    // Get a image from server
    fun getImage(image : ImageView) {
        api.getScreenShoot().enqueue(object : Callback<ResponseBody> {
            // Get response - When a image show it
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() >= 400) {
                    showError("Can't get image from server " + response.code().toString())
                    return
                }
                // Create a bit from stream and show the image
                val myInputStream = response.body()?.byteStream()
                val myBitMap = BitmapFactory.decodeStream(myInputStream)
                runOnUiThread {
                    image.setImageBitmap(myBitMap)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
               //showError("Can't get image from server")
            }
        })
    }

    fun showError(msg: String) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, msg, duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    // Send a post command
    fun sendCommand() {
        // Create HttpURLConnection for post json
        myConnection.requestMethod = "POST"
        myConnection.setRequestProperty("Content-Type", "application/json; utf-8")
        myConnection.setRequestProperty("Accept", "application/json")
        myConnection.doOutput = true;

        // create json and send it to server
        val json: String =
            "{\"aileron\":$aileron,\n\"rudder\":$rudder,\n\"elevator\":$elevator,\n\"throttle\":$throttle\n}"
        val rb: RequestBody = RequestBody.create(MediaType.parse("application/json"), json)
        api.postCommand(rb).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    if (response.code()>300) {
                        showError("POST command is failed ")
                    }
                } catch (e: java.lang.Exception) {
                    showError("POST command is failed")
                }
                return
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showError("POST command is failed (onFailure)")
                return
            }
        })
    }

}

