package com.example.appmobilegrid.api

import retrofit2.http.GET
import retrofit2.http.Query

interface MapServiceApi {
    @GET("/size")
    suspend fun getSize(): MapSize

    @GET("/column")
    suspend fun getColumn(@Query("x") x: Int): List<MapCellStrength>

    @GET("/wilibox-column")
    suspend fun getWiliboxColumn(@Query("x") x: Int): List<MapCellWilibox>
}
