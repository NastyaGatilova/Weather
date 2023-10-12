package com.example.weather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.databinding.ActivityDataDetailBinding
import com.example.weather.db.MyDbManager
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random


class DataViewModel(application: Application): AndroidViewModel(application) {
    private val listData = MutableLiveData<List<Weather>>()






    fun getListData(): LiveData<List<Weather>> {
        return listData
    }





///РАБОТА С DataDetailActivity
    @RequiresApi(Build.VERSION_CODES.O)
    fun requestList(city: String, temp:String, applicationContext: Context, binding: ActivityDataDetailBinding) {

        val url = "https://api.openweathermap.org/data/2.5/forecast?" +
                "q=$city" +
                "&appid=${API_key}" +
                "&units=metric"


        val queue = Volley.newRequestQueue(applicationContext)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->

                val updateList = parseS(result, city, temp)
                listData.value = updateList
                binding.loader.visibility = View.GONE

            },
            { error ->

                Log.d("MyLog3", "Error: $error")
                binding.loader.visibility = View.VISIBLE


            }
        )
        queue.add(request)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseS(result: String, helpCity: String , helpTemp: String): List<Weather> {
        val list = ArrayList<Weather>()
        val mainObject = JSONObject(result)

        val arrList = mainObject.getJSONArray("list")
        for (i in 0 until arrList.length()) {
            val mas_item = arrList[i] as JSONObject
            val dt = mas_item.getString("dt")

            val dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(dt.toLong()), ZoneId.systemDefault())
            val dayOfWeek =
                dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).substring(0, 2)
            val time = dateTime.toLocalTime()


            val temp_min =
                mas_item.getJSONObject("main").getString("temp_min").toDouble().roundToInt()
                    .toString()
            val temp_max =
                mas_item.getJSONObject("main").getString("temp_max").toDouble().roundToInt()
                    .toString()
            val temp_min_max = "$temp_min°C/$temp_max°C"


            val icon = mas_item.getJSONArray("weather").getJSONObject(0).getString("icon")
            val image2 =
                "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"



            val item = Weather(
                "${helpCity.capitalize()}",
                "${helpTemp} °C",
                "${dayOfWeek.capitalize()}\n$time",
                "$temp_min_max",
                "$image2"
            )

            list.add(item)
        }
        return list
    }








}
