package com.example.weather


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView


lateinit var helpWeather:Weather

val weatherList = ArrayList<Weather>()
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


        holder.itemView.setOnClickListener {

           helpWeather = weatherList[position]


            val intent = Intent(holder.itemView.context, DataDetailActivity::class.java)
            holder.itemView.context.startActivity(intent)


        }

        }




    override fun getItemCount(): Int {
        return weatherList.size
    }


   

    fun addCity(weather: Weather){
        weatherList.add(weather)
        notifyDataSetChanged()

    }



    fun deleteItem(position: Int) {


        weatherList.removeAt(position)

        notifyItemRemoved(position)

        notifyItemRangeChanged(position, weatherList.size)


    }


}