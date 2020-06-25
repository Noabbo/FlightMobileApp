package com.example.flightmobileapp
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
    private var toast: Toast? = null
    private var errorGetInRow: Int = 0
    private var errorServerInRow: Int = 0
    private var errorSetInRow: Int = 0
    var flagSimulatorActivity: Boolean = true


    // All Joystick parameters setters
    private var aileron: Double = 0.0
    private var elevator: Double = 0.0
    private var throttle: Double = 0.0
    private var rudder: Double = 0.0

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
                if(!flagSimulatorActivity) {
                    return
                }
                if (response.code() >= 300 && errorGetInRow > 20) {
                    showError("Many errors were received from the image server-\n" +
                            "Please return to the login screen", 1)
                    errorGetInRow++
                    return
                } else if (response.code() >= 300) {
                    showError("Can't get image from server", 0)
                    errorGetInRow++
                    return
                }
                // Create a bit from stream and show the image
                val myInputStream = response.body()?.byteStream()
                val myBitMap = BitmapFactory.decodeStream(myInputStream)
                runOnUiThread {
                    image.setImageBitmap(myBitMap)
                }
                errorGetInRow = 0
                errorServerInRow =0
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if(!flagSimulatorActivity) {
                    return
                }
                showError("Can't get image from server (onFailure)", 0)
                if (errorServerInRow > 10)
                    showError("Connection with server is problematic- \n" +
                            " please return to login screen", 1)
                errorServerInRow++
            }
        })
    }

    // Displays notes  for user, in the center of the screen for 2 seconds
    fun showError(msg: String, status: Int) {
        if (toast != null) {
            toast?.cancel()
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.CENTER, 0, 0)

        if (status ==1 ) {
            val v =
                toast?.view?.findViewById<View>(R.id.message) as TextView
            v.setTextColor(Color.RED)
        }
        toast?.show()
    }

    // Send a post command
    fun sendCommand() {
        // Create HttpURLConnection for post json
        myConnection.requestMethod = "POST"
        myConnection.setRequestProperty("Content-Type", "application/json; utf-8")
        myConnection.setRequestProperty("Accept", "application/json")
        myConnection.doOutput = true

        val aileronStr = "%.6f".format(aileron)
        val rudderStr = "%.6f".format(rudder)
        val elevatorStr = "%.6f".format(elevator)
        val throttleStr = "%.6f".format(throttle)

        // Create json and send it to server
        val json: String =
            "{\"aileron\":" + aileronStr + ",\n\"rudder" + "\":" + rudderStr +
                    ",\n\"elevator\":" + elevatorStr + ",\n\"throttle\":" + throttleStr + "\n}"
        val rb: RequestBody = RequestBody.create(MediaType.parse("application/json"), json)

        api.postCommand(rb).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                validateResponse(response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if(!flagSimulatorActivity) {
                    return
                }
                showError("POST command failed (onFailure)", 0)
                return
            }
        })
    }

    fun validateResponse(response: Response<ResponseBody>) {
        if(!flagSimulatorActivity) {
            return
        }

        try {
            if (response.code() == 200) {
                errorSetInRow = 0
                return
            }
            if (errorSetInRow > 30) {
                showError("Many errors were received from the post commend\n" +
                        "Please return to the login screen",1)
                print("num - " +errorSetInRow +"\n")
                return
            }
            if (response.code() >= 300) {
                showError("POST command is failed ", 0)
                print("num respo - " +errorSetInRow +"\n")
                errorSetInRow++
                return
            }
        } catch (e: java.lang.Exception) {
            showError("POST command is failed", 0)
            print("num EX- " +errorSetInRow +"\n")
            errorSetInRow++
            return
        }
        errorSetInRow = 0
    }

}

