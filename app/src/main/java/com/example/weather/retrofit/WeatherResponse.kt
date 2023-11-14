package com.example.weather.retrofit

import com.example.weather.DataViewModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "main") val main: Main,
    @Json(name = "weather") val weather: List<Weather_>,
    @Json(name = "dt") val dt: String,
)