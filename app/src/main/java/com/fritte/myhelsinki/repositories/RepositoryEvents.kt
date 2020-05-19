package com.fritte.myhelsinki.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fritte.myhelsinki.retrofit.IMyHelsinki
import com.fritte.myhelsinki.model.Error
import com.fritte.myhelsinki.model.Resource
import com.fritte.myhelsinki.model.event.Event
import kotlinx.coroutines.*

class RepositoryEvents(private val api: IMyHelsinki) {

    fun getEvents() : LiveData<Resource<List<Event>>> {

        val data : MutableLiveData<Resource<List<Event>>> = MutableLiveData()
        data.value = Resource.loading(null)

        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getEvents("en")
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        data.value = Resource.success(response.body()?.data)
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
}