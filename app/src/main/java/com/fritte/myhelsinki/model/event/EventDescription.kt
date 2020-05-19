package com.fritte.myhelsinki.model.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventDescription(
    var intro: String,
    var body: String,
    var images: List<EventImage>
) : Parcelable