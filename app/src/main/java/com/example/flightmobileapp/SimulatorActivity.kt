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

        val validUrl = client.isValidHttp(url!!)
        if (validUrl) {
            // Raises view of activity_simulator
            setContentView(R.layout.activity_simulator)
            image = findViewById(R.id.screen_shot)
            loopGetImage = true
            startShowScreenShoots()
        }

        // only for test post need to delete todo
        val testSetControl = findViewById<Button>(R.id.test_button)
        testSetControl.setOnClickListener {
            senPostRandom()
        }
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


    // Test only need to delete todo
    private fun senPostRandom() {

        var aileron = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        var elevator  = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        var throttle = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        var rudder = ThreadLocalRandom.current().nextDouble(0.0, 1.0);

        client.setJoystickParameters(aileron,elevator,throttle,rudder);
        client.sendCommand();
    }



    /** Stop asking for photos when the app is in the background or in destroy  **/
    // Start when the app is active
    override fun onStart() {
        super.onStart()
        loopGetImage = true
        startShowScreenShoots()
    }
    override fun onResume(){
        super.onResume()
        this.loopGetImage=true;
        startShowScreenShoots()
    }

    // Stop get image when the actively destroyed
    override fun onDestroy() {
        loopGetImage = false
        super.onDestroy()
    }
    // Stop get image when the actively background
    override fun onPause(){
        loopGetImage = false
        super.onPause()
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }


}