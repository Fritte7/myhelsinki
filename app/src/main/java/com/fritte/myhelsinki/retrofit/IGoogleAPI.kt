package com.fritte.myhelsinki.retrofit

import com.fritte.myhelsinki.model.googlemapapi.Directions
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGoogleAPI {
    @GET("maps/api/directions/json")
    suspend fun getDirection(@Query("origin") origin: String,
                             @Query("destination") destination: String,
                             @Query("key") key: String): Response<Directions>
}