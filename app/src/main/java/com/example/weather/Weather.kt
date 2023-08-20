package com.example.weather

import java.io.Serializable

data class Weather (

    val city: String,
    val temp: String,
    val day_week: String,
    val min_max_temp: String,
    var imageurl: String

    ):Serializable