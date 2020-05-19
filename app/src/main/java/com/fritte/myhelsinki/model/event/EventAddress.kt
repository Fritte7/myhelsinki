package com.fritte.myhelsinki.model.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventAddress(
    var street_address: String,
    var postal_code: String,
    var locality: String
) : Parcelable