package com.example.weather


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.db.MyDbManager
import org.json.JSONObject
import kotlin.math.roundToInt

class WeatherViewModel : ViewModel() {
    private val weatherData = MutableLiveData<List<Weather>>()

    fun getWeatherData(): LiveData<List<Weather>> {
        return weatherData
    }

    fun updateWeatherData(city: String, applicationContext: Context, myDbManager: MyDbManager) {

        var tempDb = ""
        var imgDb = ""

        val readData = myDbManager.readDataWhereSity(city)
        for (item in readData){
            tempDb = item.first
            imgDb = item.second
        }


        val url = "https://api.openweathermap.org/data/2.5/weather?" +
                "q=$city" +
                "&appid=${API_key}" +
                "&units=metric"
        var helpTemp = ""
        var helpImg = ""
        val queue = Volley.newRequestQueue(applicationContext)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                helpTemp = parseTemp(result)
                helpImg = parseImg(result)

                if ((helpTemp != tempDb) || (helpImg != imgDb))
                myDbManager.updateTable(helpTemp, helpImg,  city)

                val readDb = myDbManager.readDbCurData()
                weatherList.clear()
                weatherList.addAll(readDb)
                weatherData.value = readDb

            },
            { error -> }
        )
        queue.add(request)
    }

    private fun parseTemp(result: String) :String {
        val mainObject = JSONObject(result)
        val mainn = mainObject.getJSONObject("main")
        val tempTec = mainn.getString("temp").toDouble().roundToInt().toString()
        return tempTec
    }

    private fun parseImg(result: String)  :String {
        val mainObject = JSONObject(result)
        val icon = mainObject.getJSONArray("weather").getJSONObject(0).getString("icon")
        val image2 =
            "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"
        return image2
    }
}