package com.fritte.myhelsinki.model.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventDate(
    var starting_day: String,
    var ending_day: String?
) : Parcelable {
    override fun toString(): String {
        return "EventDate(starting_day='$starting_day', ending_day='$ending_day')"
    }
}