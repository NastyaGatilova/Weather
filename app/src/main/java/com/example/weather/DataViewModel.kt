package com.example.weather

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather.databinding.ActivityDataDetailBinding
import com.example.weather.retrofit.WeatherResponse
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt



@RequiresApi(Build.VERSION_CODES.O)
class DataViewModel(application: Application): MainViewModel(application) {
    private val listData = MutableLiveData<List<Weather>>()
    fun getListData(): LiveData<List<Weather>> {
        return listData
    }





///РАБОТА С DataDetailActivity

    fun requestList(city: String, temp:String, applicationContext: Context, binding: ActivityDataDetailBinding)  = viewModelScope.launch{


    try {
        val response2 = service.getDataWeather(city, "metric", API_key)



        if (response2.isSuccessful) {
            val updateList = parseS(response2.body()?.list, city, temp)
            listData.value = updateList
            binding.loader.visibility = View.GONE

        } else {
            Log.d("MyLog3", "Error: ${response2.code()}")
            binding.loader.visibility = View.VISIBLE

        }

    }catch (e:Exception){
        Log.d("--Help--", "error")
        binding.loader.visibility = View.VISIBLE
            }
                }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseS(list: List<WeatherResponse>?, helpCity: String, helpTemp: String): List<Weather> {
        val parsedList = ArrayList<Weather>()
        list?.forEach { item ->
            val dt = item.dt

            val dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(dt.toLong()), ZoneId.systemDefault())
            val dayOfWeek =
                dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).substring(0, 2)
            val time = dateTime.toLocalTime()


            val temp_min = item.main.temp_min.roundToInt().toString()
            val temp_max = item.main.temp_max.roundToInt().toString()
            val temp_min_max = "$temp_min°C/$temp_max°C"


            val icon = item.weather[0].icon
            val image2 =
                "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"

            val item = Weather(
                "${helpCity.capitalize()}",
                "${helpTemp} °C",
                "${dayOfWeek.capitalize()}\n$time",
                "$temp_min_max",
                "$image2"
            )


            parsedList.add(item)
        }
        return parsedList
    }




}
