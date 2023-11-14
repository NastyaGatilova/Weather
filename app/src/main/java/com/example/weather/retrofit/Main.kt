package com.example.weather.retrofit

import com.squareup.moshi.Json

data class Main(
    @Json(name = "temp") val temp: Double,
    ///
    @Json(name = "temp_min") val temp_min: Double,
    @Json(name = "temp_max")val temp_max: Double

)