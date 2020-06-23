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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val newLinkActivityRequestCode = 1
    private lateinit var linkViewModel: LinkViewModel
    private lateinit var url: EditText
    private var client = ClientConnect(this)

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
            connectToServer()
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

    //todo - short it to 30 line
    // On Click connect button, connecting to url that selected
    private fun connectToServer() {
        // Url that selected
        url = findViewById<EditText>(R.id.url)
        val myUrlString = url.text.toString()

        // Checking empty url
        if (myUrlString.isEmpty()) {
            client.showError("Ops - Empty Url, Please try again!")
            return
        }

        // Checking if valid http
        val validHttpRequest = client.isValidHttp(myUrlString)
        if (!validHttpRequest) {
            // Failed to connect to server
            client.showError("Ops - Login Failed, Please try again!")
            url.setText("")
        } else {

            // For test only - need to delete (todo)
            if (myUrlString == "http://test") {
                val intent = Intent(this@MainActivity, SimulatorActivity::class.java)
                intent.putExtra("url", "http://10.0.2.2:5401")
                startActivity(intent)
                return;
            }
            client.createApi()
            var myApi = client.getAPI()
            myApi.getScreenShoot().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 404) {
                        client.showError("Can't connect to server Err 404, try again!")
                        url.setText("")
                        return
                    }
                    // succeeded connecting to server:
                    val intent = Intent(this@MainActivity, SimulatorActivity::class.java)
                    intent.putExtra("url", myUrlString)
                    startActivity(intent)
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    client.showError("Can't connect to server (onFailure), try again!\n")
                    return
                }
            })
        }
    }
    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }
}