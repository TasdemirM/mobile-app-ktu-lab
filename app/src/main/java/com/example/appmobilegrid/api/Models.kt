package com.example.appmobilegrid.api

data class MapSize(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int
)

data class MapCellStrength(
    val x: Int,
    val y: Int,
    val sensor: String,
    val strength: Int
)

data class MapCellWilibox(
    val x: Int,
    val y: Int,
    val strength1: Int,
    val strength2: Int,
    val strength3: Int
)
