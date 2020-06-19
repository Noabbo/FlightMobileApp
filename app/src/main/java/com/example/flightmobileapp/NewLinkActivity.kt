package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NewLinkActivity : AppCompatActivity() {
    private lateinit var editLinkView: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_link)
        editLinkView = findViewById(R.id.url)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = LinkListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val button = findViewById<Button>(R.id.btn_connect)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editLinkView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val link = editLinkView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, link)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.linklistsql.REPLY"
    }
}