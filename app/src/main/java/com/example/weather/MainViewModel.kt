package com.example.weather


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather.db.MyDbManager
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

const val API_key = "1bb93494997fe83bb6d678b29f57d199"

class MainViewModel(application: Application): AndroidViewModel(application) {
    val myDbManager = MyDbManager(application)



    val listUpdateCity = MutableLiveData<List<Weather>>()




    init {


        val readCity = startDb(myDbManager )
        val readData = myDbManager.readDbCurData()

        if (readCity.isNotEmpty()) {

            for (item in readCity) {
               request(item, myDbManager, readData)
            }
        }
    }



    fun getlistUpdateCity(): LiveData<List<Weather>> {
        return listUpdateCity
    }




    fun startDb(myDbManager: MyDbManager): MutableList<String> {

        myDbManager.openDb()
        val readCity = myDbManager.readTable()

        return readCity
    }

    fun readListFromDb(myDbManager: MyDbManager): MutableList<Weather> {

        myDbManager.openDb()
        val readList = myDbManager.readDbCurData()

        return readList
    }



    fun request(city: String, myDbManager: MyDbManager, readData: MutableList<Weather>) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val converterFactory = MoshiConverterFactory.create(moshi)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(converterFactory)
            .build()
        val service = retrofit.create(ApiService::class.java)

        val call = service.getWeather(city, API_key, "metric")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    val helpTemp = weather?.main?.temp?.roundToInt().toString()
                    val helpImg = weather?.weather?.get(0)?.icon?.let { icon ->
                        "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"
                    } ?: ""


                    myDbManager.updateTable(helpTemp, helpImg, city)
                    listUpdateCity.value = readData
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("--Help--", "Error!!!!")
                listUpdateCity.value = readData
            }
        })
    }

    interface ApiService {
        @GET("weather")
        fun getWeather(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String
        ): Call<WeatherResponse>
    }
    @JsonClass(generateAdapter = true)
    data class WeatherResponse(
        @Json(name = "main") val main: Main,
        @Json(name = "weather") val weather: List<Weather_>,
        @Json(name = "dt") val dt: String
    )

    data class Main(
        @Json(name = "temp") val temp: Double
    )

    data class Weather_(
        @Json(name = "icon") val icon: String
    )




    private fun dateTranslation(unixTime: Long): String {


        val date = Date(unixTime * 1000L)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        val formattedDate = format.format(date)

        return formattedDate

    }







}








