package com.example.appmobilegrid.data

import com.example.appmobilegrid.api.MapServiceApi
import com.example.appmobilegrid.api.MapSize

class MapRepository(private val api: MapServiceApi) {
    suspend fun loadGrid(): Pair<MapSize, Map<Pair<Int, Int>, Map<String, Int>>> {
        val size = api.getSize()
        val grid = mutableMapOf<Pair<Int, Int>, MutableMap<String, Int>>()
        for (x in size.minX..size.maxX) {
            val column = api.getColumn(x)
            for (cell in column) {
                val key = cell.x to cell.y
                val sensors = grid.getOrPut(key) { mutableMapOf() }
                sensors[cell.sensor] = cell.strength
            }
        }
        return size to grid
    }
}
