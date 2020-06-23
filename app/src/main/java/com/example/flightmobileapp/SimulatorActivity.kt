package com.example.flightmobileapp

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.gson.GsonBuilder
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_simulator.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.cos
import kotlin.math.sin

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

        setSeekBars()
        setJoystick()

    }

    private fun setSeekBars() {
        var rudderSeekBar = findViewById<SeekBar>(R.id.seekBarRudder)
        rudderSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //There is no need to check if the margin is more then 1%
                val realProgress = (progress - 50) / 50.0
                //TODO send realProgress value to server
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        var throttleSeekBar = findViewById<SeekBar>(R.id.seekBarThrottle)
        throttleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //There is no need to check if the margin is more then 1%
                val realProgress = progress / 100.0
                //TODO send realProgress value to server
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setJoystick() {
        var joystickView = findViewById<JoystickView>(R.id.joystickView)
        joystickView.setOnMoveListener(object : JoystickView.OnMoveListener{
            var currAileron = 0.0
            var currElevator = 0.0
            override fun onMove(angle: Int, strength: Int) {
                if (angle == 0 && strength == 0) {
                    this.currAileron = 0.0
                    this.currElevator = 0.0
                    //TODO send currAileron to server
                    //TODO send currElevator to server
                } else {
                    val realStrength = strength / 100.0
                    val angleInRadians = angle * Math.PI / 180.0
                    val aileron = realStrength * cos(angleInRadians)
                    val elevator = realStrength * sin(angleInRadians)
                    //Check if margin is more then 1%
                    val aileronDelta = currAileron - aileron
                    val elevatorDelta = currElevator - elevator
                    if (aileronDelta > 0.01 || aileronDelta < -0.01) {
                        currAileron = aileron
                        //TODO send currAileron to server
                    }
                    if (elevatorDelta > 0.01 || elevatorDelta < -0.01) {
                        currElevator = elevator
                        //TODO send currElevator to server
                    }
                }
            }
        })
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