package com.example.weather

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt
val liveDataCur = MutableLiveData<String>()
class DataDetailActivity : AppCompatActivity() {

   var rcViewDataD: RecyclerView? = null
    var cityDataD: TextView? = null
    var tempDataD: TextView? = null
    var imageurlDataD: ImageView? = null
    lateinit var loader: CircularProgressView

    
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_detail)

        cityDataD = findViewById(R.id.cityDataD)
        tempDataD = findViewById(R.id.tempDataD)
        imageurlDataD = findViewById(R.id.imageurlDataD)
        rcViewDataD = findViewById(R.id.rcViewDataD)
        loader = findViewById(R.id.loader)

        initRcView2()

        cityDataD?.text = helpWeather.city
        tempDataD?.text = "${helpWeather.temp}"
        Picasso.get().load(helpWeather.imageurl).into(imageurlDataD)


        requestList(helpWeather.city)




    }




    fun initRcView2(){
        rcViewDataD?.layoutManager = LinearLayoutManager(this@DataDetailActivity) //настройка rcview по вертикали
        rcViewDataD?.adapter = adapterDay


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestList(city: String) {

        val url=  "https://api.openweathermap.org/data/2.5/forecast?" +
                "q=$city" +
                "&appid=${API_key}" +
                "&units=metric"


        val queue = Volley.newRequestQueue(applicationContext)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->

                val updateList =  parseS(result)
                adapterDay.addList(updateList as ArrayList<Weather>)
                loader.visibility = View.GONE

            },
            {
                    error ->

                Log.d("MyLog3", "Error: $error")
                loader.visibility = View.VISIBLE
                Toast.makeText(this, "Отсутствие подключения к Интернету", Toast.LENGTH_SHORT).show()

            }
        )
        queue.add(request)

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseS(result: String):List<Weather>
    {
        val list = ArrayList<Weather>()
        val mainObject = JSONObject(result)

        val arrList = mainObject.getJSONArray("list")
        for (i in 0 until arrList.length()) {
            val mas_item = arrList[i] as JSONObject
            val dt = mas_item.getString("dt")

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt.toLong()), ZoneId.systemDefault())
            val dayOfWeek = dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).substring(0, 2)
            val time = dateTime.toLocalTime()



            val temp_min = mas_item.getJSONObject("main").getString("temp_min").toDouble().roundToInt().toString()
            val temp_max = mas_item.getJSONObject("main").getString("temp_max").toDouble().roundToInt().toString()
            val temp_min_max = "$temp_min°C/$temp_max°C"


            val icon = mas_item.getJSONArray("weather").getJSONObject(0).getString("icon")
            var image2 =  "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"



            val item = Weather(
                "${helpWeather.city.capitalize()}",
                "${helpWeather.temp} °C",
                "${dayOfWeek.capitalize()}\n$time",
                "$temp_min_max",
                "$image2"
            )

            list.add(item)
        }
    return list
    }
}


