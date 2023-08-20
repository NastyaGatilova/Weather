package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData

import androidx.recyclerview.widget.RecyclerView


lateinit var helpWeather:Weather

var weatherList = mutableListOf<Weather>()

class WeatherAdapter():RecyclerView.Adapter<WeatherAdapter.WeatherHolder>(){


    class WeatherHolder(item: View):RecyclerView.ViewHolder(item) {
            val city_listatem: TextView =    item.findViewById(R.id.city_listatem)
            val temp_listatem: TextView =    item.findViewById(R.id.temp_listatem)




          fun bind(weather: Weather){
                city_listatem.text = weather.city
                temp_listatem.text = weather.temp

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


