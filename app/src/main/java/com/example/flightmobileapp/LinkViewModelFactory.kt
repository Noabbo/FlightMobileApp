package com.example.flightmobileapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class LinkViewModelFactory(private val repository: LinkRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LinkViewModel::class.java)) return LinkViewModel(repository) as T
        throw IllegalArgumentException("Unknown View Model class")
    }

}