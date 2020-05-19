package com.fritte.myhelsinki.retrofit

import com.fritte.myhelsinki.model.MyHelsinkiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IMyHelsinki {
    @GET("v1/events/")
    suspend fun getEvents(@Query("language_filter") language: String): Response<MyHelsinkiResponse>
}