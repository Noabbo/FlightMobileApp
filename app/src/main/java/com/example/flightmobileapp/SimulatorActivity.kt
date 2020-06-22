package com.example.flightmobileapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class SimulatorActivity : AppCompatActivity() {
    private var loopGetImage = false
    lateinit var image : ImageView
    private var client = ClientConnect(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get url that select in login screen
        val url = intent.getStringExtra("url")

        val connected = client.connect(url!!)
        if (connected) {
            setContentView(R.layout.activity_simulator)
            image = findViewById(R.id.image1)
            loopGetImage = true
            startShowScreenShoots()
        }
    }

    private fun startShowScreenShoots() {
        Thread {
            while(loopGetImage) {
                client.getImage(image)
                Thread.sleep(500)
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