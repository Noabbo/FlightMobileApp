package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.room.Room
import com.example.flightmobileapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: LinkListAdapter
    private lateinit var linkViewModel: LinkViewModel
    private lateinit var url: EditText
    private var client = ClientConnect(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = LoginDatabase.getInstance(application).linkDao
        val repository = LinkRepository(dao)
        val factory = LinkViewModelFactory(repository)
        linkViewModel = ViewModelProvider(this, factory).get(LinkViewModel::class.java)
        binding.myViewModel = linkViewModel
        binding.lifecycleOwner = this
        initRecyclerView()

        linkViewModel.message.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(
                    this,
                    it,
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        val button = findViewById<Button>(R.id.btn_connect)
        // set on-click listener
        button.setOnClickListener {
            this.connectClick()
        }
    }

    //TODO Noa's Part!
    private fun connectClick() {
        val uri = findViewById<TextView>(R.id.link)
        linkViewModel.saveAndConnect()
        connectToServer()
    }

    private fun initRecyclerView() {
        binding.linkRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LinkListAdapter { selectedItem: Link -> listItemClicked(selectedItem) }
        binding.linkRecyclerView.adapter = adapter
        displaySubscribersList()
    }

    private fun displaySubscribersList() {
        linkViewModel.links.observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(link: Link) {
        linkViewModel.selectUrl(link)
    }


    //todo - short it to 30 line
    // On Click connect button, connecting to url that selected
    private fun connectToServer() {
        // Url that selected
        url = findViewById<EditText>(R.id.link)
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