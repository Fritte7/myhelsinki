package com.fritte.myhelsinki.model.event

import android.os.Parcelable
import com.fritte.myhelsinki.utils.DateUtil
import kotlinx.android.parcel.Parcelize
import java.lang.StringBuilder

@Parcelize
data class Event(
    var id: String,
    var info_url: String?,
    var modified_at: String,
    var name: EventName,
    var location: EventLocation,
    var description: EventDescription,
    var tags: List<EventTag>,
    var event_dates: EventDate
) : Parcelable {
    override fun toString(): String {
        return "Event(id='$id', info_url='$info_url', modified_at='$modified_at', name=$name, location=$location, description=$description, tags=$tags, event_dates: $event_dates)"
    }

    fun getName(): String? {
        var nName: String? = null
        if (!name.en.isNullOrEmpty()) {
            nName = name.en
        } else if (!name.fi.isNullOrEmpty()) {
            nName = name.fi
        } else if (!name.sv.isNullOrEmpty()) {
            nName = name.sv
        } else if (!name.zh.isNullOrEmpty()) {
            nName = name.zh
        }
        return nName
    }

    fun getEventDate(): String? {
        val strBuild = StringBuilder(DateUtil.convertISOTimeToDate(event_dates.starting_day)!!)
        if (!event_dates.ending_day.isNullOrEmpty()) {
            strBuild.append(" - ").append(DateUtil.convertISOTimeToDate(event_dates.ending_day))
        }
        return strBuild.toString()
    }
}