package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData

import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ListItemBinding


var weatherList = mutableListOf<Weather>()

class WeatherAdapter() : RecyclerView.Adapter<WeatherAdapter.WeatherHolder>() {


    class WeatherHolder(item: View) : RecyclerView.ViewHolder(item) {

        val binding = ListItemBinding.bind(item)
        fun bind(weather: Weather) = with (binding) {
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


