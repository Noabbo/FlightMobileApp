package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val newLinkActivityRequestCode = 1
    private lateinit var linkViewModel: LinkViewModel
    private lateinit var url: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = LinkListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        //linkViewModel = ViewModelProvider(this).get(linkViewModel::class.java)
        /*linkViewModel.allLinks.observe(this, Observer { links ->
            // Update the cached copy of the words in the adapter.
            links?.let { adapter.setWords(it) }
        })*/
        url = findViewById<EditText>(R.id.url)
        val connect = findViewById<Button>(R.id.btn_connect)
        connect.setOnClickListener {
            val etUrl = url.text.toString();
            if (etUrl.isEmpty()) {
                Toast.makeText(this@MainActivity,"Empty Link", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@MainActivity, etUrl, Toast.LENGTH_LONG).show()
                val intent = Intent(this, SimulatorActivity::class.java)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newLinkActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(SimulatorActivity.EXTRA_REPLY)?.let {
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

    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }
}