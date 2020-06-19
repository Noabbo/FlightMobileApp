package com.example.flightmobileapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LinkListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<LinkListAdapter.LinkViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var links = emptyList<Link>() // Cached copy of words

    inner class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linkViewHolder: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return LinkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val current = links[position]
        holder.linkViewHolder.text = current.link
    }

    internal fun setWords(words: List<Link>) {
        this.links = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = links.size
}