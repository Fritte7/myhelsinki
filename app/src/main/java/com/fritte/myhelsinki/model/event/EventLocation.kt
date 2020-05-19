package com.fritte.myhelsinki.model.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventLocation(
    var lat: Double,
    var lon: Double,
    var address: EventAddress
) : Parcelable {
    override fun toString(): String {
        return "EventLocation(lat=$lat, lon=$lon, address=$address)"
    }
}