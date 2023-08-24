package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData

import androidx.recyclerview.widget.RecyclerView


lateinit var helpWeather: Weather

var weatherList = mutableListOf<Weather>()

class WeatherAdapter() : RecyclerView.Adapter<WeatherAdapter.WeatherHolder>() {


    class WeatherHolder(item: View) : RecyclerView.ViewHolder(item) {
        val cityListAtem: TextView = item.findViewById(R.id.cityListAtem)
        val tempListAtem: TextView = item.findViewById(R.id.tempListAtem)


        fun bind(weather: Weather) {
            cityListAtem.text = weather.city
            tempListAtem.text = weather.temp

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return WeatherHolder(view)
    }


    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {

        holder.bind(weatherList[position])

    }


    override fun getItemCount(): Int {
        return weatherList.size
    }


}


