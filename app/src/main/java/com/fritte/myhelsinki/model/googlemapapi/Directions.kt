package com.fritte.myhelsinki.model.googlemapapi

data class Directions(val routes: List<Routes>,
                      val status: String)

data class Routes(val overview_polyline: OverviewPolyline)

data class OverviewPolyline(val points: String)