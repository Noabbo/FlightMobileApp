package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SimulatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulator)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        // Disconnect from server.
        // Go back to login screen.
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}