package com.example.flightmobileapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ThreadLocalRandom


class SimulatorActivity : AppCompatActivity() {
    private var loopGetImage = false
    lateinit var image : ImageView
    private var client = ClientConnect(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get url that select in login screen
        val url = intent.getStringExtra("url")




        val connected = client.isValidHttp(url!!)
        if (connected) {
            setContentView(R.layout.activity_simulator)
            image = findViewById(R.id.screen_shot)
            loopGetImage = true
            startShowScreenShoots()
        }

        val testSetControl = findViewById<Button>(R.id.test_button)
        testSetControl.setOnClickListener {
            senPostRandom()
        }
    }

    private fun startShowScreenShoots() {
        CoroutineScope(IO).launch {
            while (loopGetImage) {
                client.getImage(image)
                delay(500)
            }
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }

    /*
    override fun onStart() {
        super.onStart()
        loopGetImage = true
        startShowScreenShoots()
    }

    override fun onDestroy() {
        loopGetImage = false
        super.onDestroy()
    }
    override fun onPause(){
        loopGetImage = false
        super.onPause()
    }
     */


    private fun senPostRandom() {

        var aileron = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        var elevator  = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        var throttle = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        var rudder = ThreadLocalRandom.current().nextDouble(0.0, 1.0);

        client.setJoystickParameters(aileron,elevator,throttle,rudder);
        client.sendCommand();
    }




}