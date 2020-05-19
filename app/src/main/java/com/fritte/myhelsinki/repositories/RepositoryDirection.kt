package com.fritte.myhelsinki.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.fritte.myhelsinki.R
import com.fritte.myhelsinki.application.MyHelsinkiApp
import com.fritte.myhelsinki.retrofit.IGoogleAPI
import com.fritte.myhelsinki.model.Error
import com.fritte.myhelsinki.model.Resource
import com.fritte.myhelsinki.model.googlemapapi.Directions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import javax.inject.Inject

class RepositoryDirection {

    @Inject lateinit var googleAPI: IGoogleAPI
    @Inject lateinit var context: Context

    fun getDirection(from: LatLng, to: LatLng) : LiveData<Resource<Directions>> {
        MyHelsinkiApp.instance.appComponent.inject(this)

        val data: MutableLiveData<Resource<Directions>> = MutableLiveData()
        data.value = Resource.loading(null)

        CoroutineScope(Dispatchers.IO).launch {
            val response = googleAPI.getDirection(getParam(from), getParam(to), getKey())
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        data.value = Resource.success(response.body())
                    } else {
                        data.value = Resource.error(Error(response.code(), response.message()))
                    }
                } catch (e: Throwable) {
                    data.value = Resource.error(Error(Error.ERROR_CONNECTION, e.message))
                }
            }
        }
        return data
    }

    private fun getParam(latLng: LatLng): String {
        return StringBuilder(latLng.latitude.toString())
                            .append(",")
                            .append(latLng.longitude.toString())
                            .toString()
    }

    private fun getKey(): String {
        return context.resources.getString(R.string.google_maps_key)
    }
}