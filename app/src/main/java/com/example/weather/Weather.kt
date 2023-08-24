package com.example.weather

import java.io.Serializable

data class Weather(

    val city: String,
    val temp: String,
    val dayWeek: String,
    val minMaxTemp: String,
    var imageurl: String

) : Serializable