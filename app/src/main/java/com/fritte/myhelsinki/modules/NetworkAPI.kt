package com.fritte.myhelsinki.modules

import com.fritte.myhelsinki.retrofit.IGoogleAPI
import com.fritte.myhelsinki.retrofit.IMyHelsinki
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkAPI {

    @Provides
    @Singleton
    fun provideGoogleAPI(): IGoogleAPI {
        return Retrofit.Builder()
                       .baseUrl("https://maps.googleapis.com/")
                       .addConverterFactory(MoshiConverterFactory.create())
                       .build()
                       .create(IGoogleAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMyHelsinkiAPI(): IMyHelsinki {
        return Retrofit.Builder()
            .baseUrl("http://open-api.myhelsinki.fi/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(IMyHelsinki::class.java)
    }
}