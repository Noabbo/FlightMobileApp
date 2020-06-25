package com.example.flightmobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimulatorActivity : AppCompatActivity() {
    private var loopGetImage = false
    lateinit var image : ImageView
    private var client = ClientConnect(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get url that select in login screen
        val url = intent.getStringExtra("url")

        val validUrl = client.isValidHttp(url!!)
        if (validUrl) {
            // Raises view of activity_simulator
            setContentView(R.layout.activity_simulator)
            image = findViewById(R.id.screen_shot)
            loopGetImage = true
            startShowScreenShoots()
        }
        setSeekBars()
        setJoystick()

    }

    private fun setSeekBars() {
        val rudderSeekBar = findViewById<SeekBar>(R.id.seekBarRudder)
        rudderSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //There is no need to check if the margin is more then 1%
                val realProgress = (progress - 50) / 50.0
                client.setRudder(realProgress)
                client.sendCommand()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val throttleSeekBar = findViewById<SeekBar>(R.id.seekBarThrottle)
        throttleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //There is no need to check if the margin is more then 1%
                val realProgress = progress / 100.0
                client.setThrottle(realProgress)
                client.sendCommand()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setJoystick() {
        val joystickView = findViewById<JoystickView>(R.id.joystickView)
        joystickView.setOnMoveListener(object : JoystickView.OnMoveListener{
            var currAileron = 0.0
            var currElevator = 0.0
            override fun onMove(angle: Int, strength: Int) {
                var changed = false
                val realStrength = strength / 100.0
                val angleInRadians = angle * Math.PI / 180.0
                val aileron = realStrength * cos(angleInRadians)
                val elevator = realStrength * sin(angleInRadians)
                //Check if margin is more then 1%
                val aileronDelta = currAileron - aileron
                val elevatorDelta = currElevator - elevator
                if (aileronDelta > 0.01 || aileronDelta < -0.01 || aileron == 0.0) {
                    currAileron = aileron
                    client.setAileron(currAileron)
                    changed = true
                }
                if (elevatorDelta > 0.01 || elevatorDelta < -0.01 || elevator == 0.0) {
                    currElevator = elevator
                    client.setElevator(currElevator)
                    changed = true
                }
                if (changed) {
                    client.sendCommand()
                }
            }
        })
    }

    // Start ask for image asyc
    private fun startShowScreenShoots() {
        CoroutineScope(IO).launch {
            while (loopGetImage) {
                client.getImage(image)
                delay(500)
            }
        }
    }



    /** Stop asking for photos when the app is in the background or in destroy  **/
    // Start when the app is active
    override fun onStart() {
        super.onStart()
        this.client.flagSimulatorActivity = true
        loopGetImage = true
        startShowScreenShoots()
    }
  
    override fun onResume(){
        super.onResume()
        this.client.flagSimulatorActivity = true
        this.loopGetImage=true
        startShowScreenShoots()
    }

    // Stop get image when the actively destroyed
    override fun onDestroy() {
        loopGetImage = false
        this.client.flagSimulatorActivity = false
        super.onDestroy()
    }
    // Stop get image when the actively background
    override fun onPause(){
        loopGetImage = false
        this.client.flagSimulatorActivity = false
        super.onPause()
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }
}