package com.example.appmobilegrid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appmobilegrid.api.Network
import com.example.appmobilegrid.data.MapRepository

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val repo = MapRepository(Network.api)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
