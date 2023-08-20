package com.example.weather

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.db.MyDbManager
import org.json.JSONObject
import kotlin.math.roundToInt





var cityPerem: String = ""
var tempPerem: String = ""
class ChoiseCityActivity : MainActivity() {

    private var act_choise_city_btn: Button? = null
    private var act_choise_city_editText: EditText? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choise_city)

        initWidgets()

        act_choise_city_btn?.setOnClickListener {

            cityPerem = act_choise_city_editText?.getText().toString().trim().toLowerCase()

            if (cityPerem == "") {
                Toast.makeText(this, "Введите название города!", Toast.LENGTH_SHORT).show()
            } else{

                if (myDbManager.checkCityExists(cityPerem.capitalize()))
                    Toast.makeText(this, "Такой город уже существует в списке!", Toast.LENGTH_SHORT).show()
                else
                {
                    request(cityPerem)
                }


            }

        }



    }


    private fun initWidgets(){
        act_choise_city_btn = findViewById(R.id.act_choise_city_btn)
        act_choise_city_editText = findViewById(R.id.act_choise_city_editText)
        act_choise_city_editText?.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    }


    private fun request(city: String){

        val url = "https://api.openweathermap.org/data/2.5/weather?" +
                "q=$city" +
                "&appid=${API_key}"+
                "&units=metric"
        val queue = Volley.newRequestQueue(applicationContext)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                    result ->

               parse(result)
            },
            {
                    error ->
                if(error.toString() == "com.android.volley.ClientError" )    Toast.makeText(this, "Такого города не существует!", Toast.LENGTH_SHORT).show()
                else {
                    Toast.makeText(this, "Отсутствие подключения к Интернету", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Если проблема не решена: openweathermap.org/api", Toast.LENGTH_LONG).show()
                }



            }
        )
        queue.add(request)

    }

    private fun parse(result: String) {
        val mainObject = JSONObject(result)

        val mainn = mainObject.getJSONObject("main")
        val tempTec = mainn.getString("temp").toDouble().roundToInt()
        tempPerem =tempTec.toString()

        val icon = mainObject.getJSONArray("weather").getJSONObject(0).getString("icon")

        val image2 =  "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"
        val item = Weather(
            "${cityPerem.capitalize()}",
            "$tempTec °C",
            "ВТ",
            " / ",
            "$image2"
        )

        myDbManager.insertToDbCurData(item)

        val editIntent = Intent().apply{
            putExtra("weather", item)
        }
        setResult(RESULT_OK, editIntent)
        finish()


    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        myDbManager.closeDb()
        finish()
    }

}