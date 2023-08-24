package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


val weatherDayList = ArrayList<Weather>()

class DayWeatherAdapter() : RecyclerView.Adapter<DayWeatherAdapter.DayWeatherHolder>() {


    class DayWeatherHolder(item: View) : RecyclerView.ViewHolder(item) {
        val day: TextView = item.findViewById(R.id.day)
        val minMaxTemp: TextView = item.findViewById(R.id.minMaxTemp)
        val dataItemImg: ImageView = item.findViewById(R.id.dataItemImg)


        fun bind(weather: Weather) {
            day.text = weather.dayWeek
            minMaxTemp.text = weather.minMaxTemp
            Picasso.get().load(weather.imageurl).into(dataItemImg)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayWeatherHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.data_item, parent, false)
        return DayWeatherHolder(view)
    }


    override fun onBindViewHolder(holder: DayWeatherHolder, position: Int) {
        holder.bind(weatherDayList[position])

    }


    override fun getItemCount(): Int {
        return weatherDayList.size
    }


}