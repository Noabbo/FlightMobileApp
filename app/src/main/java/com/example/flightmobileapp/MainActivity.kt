package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.flightmobileapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: LinkListAdapter
    private lateinit var linkViewModel: LinkViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val dao = LoginDatabase.getInstance(application).linkDao
        val repository = LinkRepository(dao)
        val factory = LinkViewModelFactory(repository)
        linkViewModel = ViewModelProvider(this,factory).get(LinkViewModel::class.java)
        binding.myViewModel = linkViewModel
        binding.lifecycleOwner = this
        initRecyclerView()

        linkViewModel.message.observe(this, Observer {it->
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
            connectClick()
        }
    }

    private fun initRecyclerView(){
        binding.linkRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LinkListAdapter { selectedItem:Link->listItemClicked(selectedItem)}
        binding.linkRecyclerView.adapter = adapter
        displaySubscribersList()
    }

    private fun displaySubscribersList(){
        linkViewModel.links.observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(link: Link){
        linkViewModel.selectUrl(link)
    }

    private fun connectClick() {
        val uri = findViewById<TextView>(R.id.link)
        linkViewModel.saveAndConnect()
        if (uri.text.isNotEmpty()) {
            val i = Intent(this, SimulatorActivity::class.java)
            startActivity(i)
        }
    }
}