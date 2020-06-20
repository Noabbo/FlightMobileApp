package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback

class SimulatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulator)
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