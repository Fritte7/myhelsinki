package com.fritte.myhelsinki.model.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventName(
    var en: String?,
    var fi: String?,
    var sv: String?,
    var zh: String?
) : Parcelable {
    override fun toString(): String {
        return "EventName(en='$en', fi='$fi', sv='$sv', zh='$zh')"
    }
}
