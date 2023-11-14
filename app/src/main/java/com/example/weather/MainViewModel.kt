package com.example.weather



import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather.db.MyDbManager
import com.example.weather.retrofit.ApiService
import com.example.weather.retrofit.WeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.reflect.typeOf

const val API_key = "1bb93494997fe83bb6d678b29f57d199"

open class MainViewModel(application: Application): AndroidViewModel(application) {
    val myDbManager = MyDbManager(application)

    private val sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val listUpdateCity = MutableLiveData<List<Weather>>()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val converterFactory = MoshiConverterFactory.create(moshi)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(converterFactory)
        .build()
    val service = retrofit.create(ApiService::class.java)

    val listTempImg = mutableListOf<Weather>()




    init {
        val readCity = startDb(myDbManager )
        val readData = myDbManager.readDbCurData()




        val nowTime = System.currentTimeMillis()
        val lastTime = getTime()


        if (readCity.isNotEmpty()) {
            if (checkTime( lastTime,nowTime )){
                for (item in readCity) {
                    request(item, myDbManager, readData, service, application)
                }
            }
            else  listUpdateCity.value = readData

        }


    }



    fun saveTime() {

        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit().putLong("time4", currentTime).apply()


    }


    fun checkTime(lastUpdateTime: Long, nowTime: Long) :Boolean{

        val tenMinutesInMillis = 10 * 60 * 1000



        if (nowTime - lastUpdateTime < tenMinutesInMillis) {
            return false
        } else {
            return true
        }
    }

    fun getTime() :Long{

        return sharedPreferences.getLong("time4", 0)

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






    fun request(
        city: String, myDbManager: MyDbManager, readData: MutableList<Weather>,
        service: ApiService,applicationContext: Context
    ) {


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


                   saveTime()



                    val item = Weather(
                        "${city}",
                        "$helpTemp°C",
                        "",
                        "",
                        "${helpImg}"
                    )

                    listTempImg.add(item)
                    myDbManager.updateTable(helpTemp, helpImg, city)
                    listUpdateCity.value = listTempImg



                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("--Help--", "Error from MainViewModel!!!!")
                listUpdateCity.value = readData
            }
        })
    }






    private fun dateTranslation(unixTime: Long): String {

        val date = Date(unixTime)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = sdf.format(date)

        return formattedTime

    }







}








