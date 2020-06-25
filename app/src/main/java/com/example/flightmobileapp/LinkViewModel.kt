package com.example.flightmobileapp

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class LinkViewModel (private val repository: LinkRepository) : ViewModel(), Observable {
    val links = repository.listLinks
    private var fromList = -1

    @Bindable
    val etLink = MutableLiveData<String>()

    @Bindable
    val connectButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        connectButtonText.value = "Connect"
    }

    fun saveAndConnect() {
        if (etLink.value == null) {
            statusMessage.value = Event("Please enter URL")
        } else {
            val l = etLink.value!!
            // Check if link is valid!!
            // Delete existing link to be placed again
            if (fromList > -1) {
                deleteLink(Link(fromList, l))
            } else if (repository.isLinkInRoom(l)) {
                deleteLink(repository.findLink(l))
            }
            // Insert link to database.
            insertLink(Link(0, l))
            fromList = -1
            etLink.value = null
        }
    }

    fun selectUrl(link : Link) {
        etLink.value = link.link
        fromList = link.id
    }

    private fun insertLink(link: Link) = viewModelScope.launch {
        val newRowId = repository.insert(link)
        if (newRowId < 0) {
            statusMessage.value = Event("Error Occurred")
        }
    }

    private fun deleteLink(link: Link) = viewModelScope.launch {
        val newRowId = repository.deleteLink(link)
        if (newRowId < 0) {
            statusMessage.value = Event("Error Occurred")
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

}