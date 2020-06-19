package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val newLinkActivityRequestCode = 1
    private lateinit var linkViewModel: LinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newLinkActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewLinkActivity.EXTRA_REPLY)?.let {
                val link = Link(
                    System.currentTimeMillis(),
                    it
                )
                linkViewModel.insert(link)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }
}