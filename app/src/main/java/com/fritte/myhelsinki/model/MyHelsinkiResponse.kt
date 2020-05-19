package com.fritte.myhelsinki.model

import com.fritte.myhelsinki.model.event.Event

data class MyHelsinkiResponse(
    var data: List<Event>? = null
)