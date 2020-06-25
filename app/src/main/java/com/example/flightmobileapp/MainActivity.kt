package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    private fun connectClick() {
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
        url = findViewById(R.id.link)
        val myUrlString = url.text.toString()

        // Checking empty url
        if (myUrlString.isEmpty()) {
            client.showError("Ops - Empty Url, Please try again!",0)
            return
        }
        // Checking if valid http
        val validHttpRequest = client.isValidHttp(myUrlString)
        if (!validHttpRequest) {
            // Failed to connect to server
            client.showError("Oops - Login Failed, Please try again!", 0)
            url.setText("")
        } else {
            client.createApi()
            val myApi = client.getAPI()
            myApi.getScreenShoot().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 404) {
                        client.showError("Can't connect to server Error 404, try again!", 0)
                        url.setText("")
                        return
                    }
                    // succeeded connecting to server:
                    val intent = Intent(this@MainActivity, SimulatorActivity::class.java)
                    intent.putExtra("url", myUrlString)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    client.showError("Can't connect to server, try again!", 0)
                    return
                }
            })
        }
    }
}