package com.fritte.myhelsinki.model.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventImage(
    var url: String
) : Parcelable