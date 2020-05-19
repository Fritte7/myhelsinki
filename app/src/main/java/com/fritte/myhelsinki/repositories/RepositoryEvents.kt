package com.fritte.myhelsinki.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fritte.myhelsinki.application.MyHelsinkiApp
import com.fritte.myhelsinki.retrofit.IMyHelsinki
import com.fritte.myhelsinki.model.Error
import com.fritte.myhelsinki.model.Resource
import com.fritte.myhelsinki.model.event.Event
import kotlinx.coroutines.*
import javax.inject.Inject

class RepositoryEvents {

    @Inject
    lateinit var myHelsinkiAPI: IMyHelsinki

    fun getEvents() : LiveData<Resource<List<Event>>> {
        MyHelsinkiApp.instance.appComponent.inject(this)

        val data : MutableLiveData<Resource<List<Event>>> = MutableLiveData()
        data.value = Resource.loading(null)

        CoroutineScope(Dispatchers.IO).launch {
            val response = myHelsinkiAPI.getEvents("en")
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