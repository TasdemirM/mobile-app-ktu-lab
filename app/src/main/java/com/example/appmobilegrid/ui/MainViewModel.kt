package com.example.appmobilegrid.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmobilegrid.api.MapSize
import com.example.appmobilegrid.data.MapRepository
import com.example.appmobilegrid.data.NearestNeighbor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repo: MapRepository) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val error: String? = null,
        val size: MapSize? = null,
        val grid: Map<Pair<Int, Int>, Map<String, Int>> = emptyMap(),
        val located: Pair<Int, Int>? = null
    )

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state

    fun load() {
        _state.value = _state.value?.copy(loading = true, error = null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (size, grid) = repo.loadGrid()
                _state.postValue(
                    _state.value?.copy(
                        loading = false,
                        size = size,
                        grid = grid
                    )
                )
            } catch (e: Exception) {
                _state.postValue(
                    _state.value?.copy(
                        loading = false,
                        error = e.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    fun locate(targetRssi: Map<String, Int>) {
        val grid = _state.value?.grid ?: return
        val best = NearestNeighbor.findClosest(grid, targetRssi)
        _state.postValue(_state.value?.copy(located = best))
    }
}
