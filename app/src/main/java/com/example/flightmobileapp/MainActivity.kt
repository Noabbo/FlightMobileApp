package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
    private var client = ClientConnect()

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


        // !!!! Anonymous function too long, I changed a bit and separated to connectToServer
        //          Listens through onclick xml  !!!!

        val connect = findViewById<Button>(R.id.btn_connect)
        connect.setOnClickListener {
            connectToServer(it)
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


    // On Click connect button, connecting to url that selected
    private fun connectToServer(view: View) {
        // Try to connect to server
        url = findViewById<EditText>(R.id.url)
        val myUrlString = url.text.toString()
        if (myUrlString.isEmpty()) {
            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(this@MainActivity, "Empty Link", duration)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
            val connected = client.connect(myUrlString)
            if (!connected) {
                // Failed to connect to server
                val text = "Ops - Login failed, please try again!"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(this@MainActivity, text, duration)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                url.setText("")
            } else {
                // Succeeded connecting to server
                val intent = Intent(this, SimulatorActivity::class.java).apply {
                    putExtra("url", myUrlString)

                }
                startActivity(intent)
            }

        }

    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }
}