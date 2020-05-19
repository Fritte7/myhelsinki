package com.fritte.myhelsinki.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil{
    fun convertISOTimeToDate(isoTime: String?): String? {
        if (isoTime.isNullOrEmpty()) {
            return null
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val convertedDate: Date?
        var formattedDate: String? = null
        try {
            convertedDate = sdf.parse(isoTime)
            formattedDate = SimpleDateFormat("MMM dd HH:mm, YYYY", Locale.getDefault()).format(convertedDate!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedDate
    }
}