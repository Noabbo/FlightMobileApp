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

class ClientConnect(private var context: Context) : AppCompatActivity() {
    private lateinit var urlConnection: URL
    private lateinit var myConnection: HttpURLConnection
    private lateinit var api: ApiConnectServer

    // All Joystick parameters setters
    private var aileron: Double = 0.0
    private var elevator: Double = 0.0
    private var throttle: Double = 0.0
    private var rudder: Double = 0.0

    // only for test need to delete todo
    fun setJoystickParameters(aileron: Double, elevator: Double,
                              throttle: Double, rudder: Double ) {
        this.aileron = aileron
        this.elevator = elevator
        this.throttle = throttle
        this.rudder = rudder
    }

    /** Setter of Joystick Parameters  **/

    fun setAileron(newAileron: Double) {
       this.aileron = newAileron
   }

    fun setElevator(newElevator: Double) {
        this.elevator = newElevator
    }

    fun setThrottle(newThrottle: Double) {
        this.throttle = newThrottle
    }
    fun setRudder(newRudder: Double) {
        this.rudder = newRudder
    }

    // get api interface
    fun getAPI(): ApiConnectServer {
        return api
    }

    // Checks that the url is valid
    fun isValidHttp(url: String):Boolean {
        val tempUrl: URL
        try {
            tempUrl = URL(url)
            myConnection = tempUrl.openConnection() as HttpURLConnection

        } catch (e: Exception) {
            return false
        }
        urlConnection = tempUrl
        createApi()
        return true
    }

    // Envelopes the api interface, allows Jason, receive a url
    fun createApi() {
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
                if (response.code() >= 300) {
                    showError("Can't get image from server ")
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
            }
        })
    }
    // Displays notes  for user, in the center of the screen for 2 seconds
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

        // Create json and send it to server
        val json: String =
            "{\"aileron\":$aileron,\n\"rudder" +
                    "\":$rudder,\n\"elevator\":$elevator,\n\"throttle\":$throttle\n}"
        val rb: RequestBody = RequestBody.create(MediaType.parse("application/json"), json)
        api.postCommand(rb).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    if (response.code() >= 300) {
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

