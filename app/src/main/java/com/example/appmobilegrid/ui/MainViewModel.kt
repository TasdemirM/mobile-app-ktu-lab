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
        val size: MapSize? = null, // map bounds from backend
        val grid: Map<Pair<Int, Int>, Map<String, Int>> = emptyMap(), // map cell -> sensor RSSI
        val located: Pair<Int, Int>? = null, // latest located cell
        val manualEntries: List<ManualEntry> = emptyList() // history of manual locate attempts
    )

    data class ManualEntry(
        val id: Int,
        val inputs: Map<String, Int>,
        val located: Pair<Int, Int>?,
        val distance: Double
    )

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state

    fun load() {
        // Fetch map size and grid cells from backend.
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
        // Run nearest neighbor against current grid and record the attempt.
        val grid = _state.value?.grid ?: return
        val (best, dist) = NearestNeighbor.findClosestWithDistance(grid, targetRssi)
        val nextId = (_state.value?.manualEntries?.maxOfOrNull { it.id } ?: 0) + 1
        val updatedEntries = _state.value?.manualEntries.orEmpty() + ManualEntry(
            id = nextId,
            inputs = targetRssi,
            located = best,
            distance = dist
        )
        _state.postValue(
            _state.value?.copy(
                located = best,
                manualEntries = updatedEntries
            )
        )
    }
}
