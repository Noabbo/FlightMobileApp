package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val newLinkActivityRequestCode = 1
    private lateinit var linkViewModel: LinkViewModel
    private lateinit var editLinkView: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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