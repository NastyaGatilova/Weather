package com.example.weather

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.databinding.ActivityDataDetailBinding
import com.example.weather.retrofit.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class DataDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataDetailBinding

    private val viewModel by lazy { ViewModelProvider(this).get(DataViewModel::class.java)}

    private val adapterDay = DayWeatherAdapter()


    var helpCity = ""
    var helpTemp = ""
    var helpImageurl = ""


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initRcView2()


        helpCity = intent.getStringExtra("city").toString()
        helpTemp = intent.getStringExtra("temp").toString()
        helpImageurl = intent.getStringExtra("imageurl").toString()



        binding.cityDataD.text = helpCity
        binding.tempDataD.text = helpTemp
        Picasso.get().load(helpImageurl).into(binding.imageurlDataD)

        viewModel.getListData().observe(this, { newWeatherDayList ->

            weatherDayList.clear()
            weatherDayList.addAll(newWeatherDayList)
            adapterDay.notifyDataSetChanged()

        })




       viewModel.requestList(helpCity, helpTemp, this, binding)


    }


    private fun initRcView2() {
        binding.rcViewDataD.layoutManager =
            LinearLayoutManager(this@DataDetailActivity) //настройка rcview по вертикали
        binding.rcViewDataD.adapter = adapterDay


    }

}


