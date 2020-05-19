package com.fritte.myhelsinki.viewmodels

import android.location.Location
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.fritte.myhelsinki.model.event.Event
import com.fritte.myhelsinki.repositories.RepositoryDirection
import com.fritte.myhelsinki.repositories.RepositoryEvents

class ViewModelEvents : ViewModel() {
    private val repository: RepositoryEvents = RepositoryEvents()

    val events = repository.getEvents()
    val eventSelected: MutableLiveData<Event> = MutableLiveData()
    val currentLatLng: MutableLiveData<LatLng?> = MutableLiveData()

    val directionEventSelected = Transformations.switchMap(eventSelected) {
        if (currentLatLng.value != null && eventSelected.value != null) {
            val latLng = LatLng(eventSelected.value?.location?.lat!!, eventSelected.value?.location?.lon!!)
            RepositoryDirection().getDirection(currentLatLng.value!!, latLng)
        } else {
            null
        }
    }

    /** create notification nearby, filtering same event and less than 5km **/
    val notificationNearbyEvents: LiveData<List<Event>> = Transformations.switchMap(events) {
        if (currentLatLng.value != null && it.data != null) {
            val notif = MutableLiveData<List<Event>>()
            val list = ArrayList<Event>()
            it.data.forEach {
                var alreadyExist = false
                for (event in list) {
                    if (event.getName() == it.getName()) {
                        alreadyExist = true
                        break
                    }
                }
                if (!alreadyExist) {
                    val dist = calculateDistance(currentLatLng.value!!.latitude, currentLatLng.value!!.longitude, it.location.lat, it.location.lon)
                    if (dist < 5000) {
                        list.add(it)
                    }
                }
            }
            notif.value = list
            notif
        } else {
            null
        }
    }

    fun selectedEvent(event: Event?) {
        eventSelected.value = event
    }

    fun updateCurrentLocation(loc: LatLng) {
        currentLatLng.value = loc
    }

    /** calculate distance between two point, return in meters **/
    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }
}