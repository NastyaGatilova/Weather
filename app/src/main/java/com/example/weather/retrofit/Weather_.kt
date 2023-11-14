package com.example.weather.retrofit

import com.squareup.moshi.Json

data class Weather_(
    @Json(name = "icon") val icon: String
)