package com.fritte.myhelsinki.network

import com.fritte.myhelsinki.retrofit.IGoogleAPI
import com.fritte.myhelsinki.retrofit.IMyHelsinki
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkAPI {

    fun provideGoogleAPI(): IGoogleAPI {
        return Retrofit.Builder()
                       .baseUrl("https://maps.googleapis.com/")
                       .addConverterFactory(MoshiConverterFactory.create())
                       .build()
                       .create(IGoogleAPI::class.java)
    }

    fun provideMyHelsinkiAPI(): IMyHelsinki {
        return Retrofit.Builder()
            .baseUrl("http://open-api.myhelsinki.fi/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(IMyHelsinki::class.java)
    }
}