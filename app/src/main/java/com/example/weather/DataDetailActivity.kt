package com.example.weather

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.databinding.ActivityDataDetailBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

class DataDetailActivity : AppCompatActivity() {

private lateinit var binding: ActivityDataDetailBinding

    val adapterDay = DayWeatherAdapter()

    var helpCity = ""
    var helpTemp = ""
    var helpImageurl = ""


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initRcView2()


        helpCity = intent.getStringExtra("city").toString()
        helpTemp = intent.getStringExtra("temp").toString()
        helpImageurl = intent.getStringExtra("imageurl").toString()

        binding.cityDataD?.text = helpCity
        binding.tempDataD?.text = helpTemp
        Picasso.get().load(helpImageurl).into(binding.imageurlDataD)


        requestList(helpCity)


    }


    private fun initRcView2() {
        binding.rcViewDataD?.layoutManager =
            LinearLayoutManager(this@DataDetailActivity) //настройка rcview по вертикали
        binding.rcViewDataD?.adapter = adapterDay


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestList(city: String) {

        val url = "https://api.openweathermap.org/data/2.5/forecast?" +
                "q=$city" +
                "&appid=${API_key}" +
                "&units=metric"


        val queue = Volley.newRequestQueue(applicationContext)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->

                val updateList = parseS(result)
                addList(updateList as ArrayList<Weather>)
                binding.loader.visibility = View.GONE

            },
            { error ->

                Log.d("MyLog3", "Error: $error")
                binding.loader.visibility = View.VISIBLE
                Toast.makeText(this, "Отсутствие подключения к Интернету", Toast.LENGTH_SHORT)
                    .show()

            }
        )
        queue.add(request)

    }


    fun addList(list: ArrayList<Weather>) {
        weatherDayList.clear()
        weatherDayList.addAll(list)
        adapterDay.notifyDataSetChanged()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseS(result: String): List<Weather> {
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


