package com.example.flightmobileapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flightmobileapp.databinding.ListItemBinding

class LinkListAdapter(private val clickListener:(Link)->Unit)
    : RecyclerView.Adapter<MyViewHolder>()
{
    private val linksList = ArrayList<Link>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : ListItemBinding =
            DataBindingUtil.inflate(layoutInflater,R.layout.list_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return linksList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(linksList[position],clickListener)
    }

    fun setList(links: List<Link>) {
        linksList.clear()
        linksList.addAll(links)
    }
}

class MyViewHolder (private val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(link: Link, clickListener:(Link)->Unit) {
        binding.linkTextView.text = link.link
        binding.listItemLayout.setOnClickListener{
            clickListener(link)
        }
    }
}