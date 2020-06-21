package com.example.flightmobileapp

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SimulatorActivity : AppCompatActivity() {
    private var client = ClientConnect()
    lateinit var image : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("url")
        val connected = client.connect(url)
        if (connected) {
            setContentView(R.layout.activity_simulator)
            image = findViewById(R.id.screen_shoot)
            startShowScreenShoots(url)
        }
    }

    private fun startShowScreenShoots(url : String) {
        Thread {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            val myApi = retrofit.create(ApiConnectServer::class.java)
            while(true) {
                val body = myApi.getScreenShoot().enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val I = response.body()?.byteStream()
                        val B = BitmapFactory.decodeStream(I)
                        runOnUiThread {
                            image.setImageBitmap(B)
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        var i: Int = 5
                    }
                })
                Thread.sleep(5000)
            }
        }.start()
    }



    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }
    /*override fun OnBackPressed() {
        // Disconnect from server.
        // Go back to login screen.
        val intent = Intent(this, MainActivity::class.java)
        super.onBackPressed();
    }*/
}