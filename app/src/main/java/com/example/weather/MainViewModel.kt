package com.example.weather


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather.db.MyDbManager
import com.example.weather.retrofit.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

const val API_key = "1bb93494997fe83bb6d678b29f57d199"


val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
val converterFactory = MoshiConverterFactory.create(moshi)
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.openweathermap.org/data/2.5/")
    .addConverterFactory(converterFactory)
    .build()
val service = retrofit.create(ApiService::class.java)

@SuppressLint("SuspiciousIndentation")
open class MainViewModel(application: Application): AndroidViewModel(application) {
    val myDbManager = MyDbManager(application)

    private val sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val listUpdateCity = MutableLiveData<List<Weather>>()

    val listTempImg = mutableListOf<Weather>()



    init {
        val readCity = startDb(myDbManager)
        val readData = myDbManager.readDbCurData()

        val nowTime = System.currentTimeMillis()
        val lastUpdateTime = getTime()

        if (readCity.isNotEmpty()) {


            if (isUpdateNeeded(lastUpdateTime, nowTime)) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (item in readCity) {
                            request(item, myDbManager, readData, service)
                        }

                        saveTime()

                    }}
                    else {
                listUpdateCity.value = readData
                }
                }
            }








    fun saveTime() {

        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit().putLong("time4", currentTime).apply()


    }


    fun isUpdateNeeded(lastUpdateTime: Long, nowTime: Long) :Boolean{

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
        service: ApiService
    ) = viewModelScope.launch {

        try{

                    val weather = service.getWeather(city, API_key, "metric")
                    val helpTemp = weather?.main?.temp?.roundToInt().toString()

                    val helpImg = weather?.weather?.get(0)?.icon?.let { icon ->
                        "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"
                    } ?: ""



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
                    catch (e: Exception) {
                        listUpdateCity.value = readData
                        Log.d("--Help--", "ERROR!!!!")
                }

                }
    }



    private fun dateTranslation(unixTime: Long): String {

        val date = Date(unixTime)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = sdf.format(date)

        return formattedTime

    }
















