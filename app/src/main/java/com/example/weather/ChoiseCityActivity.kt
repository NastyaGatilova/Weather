package com.example.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.databinding.ActivityChoiseCityBinding
import org.json.JSONObject
import kotlin.math.roundToInt





var cityPerem: String = ""
var tempPerem: String = ""
class ChoiseCityActivity : MainActivity() {

private lateinit var binding: ActivityChoiseCityBinding

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoiseCityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.cityEditText.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        binding.cityBtn.setOnClickListener {

            cityPerem = binding.cityEditText.getText().toString().trim().toLowerCase()

            if (cityPerem == "") Toast.makeText(this, "Введите название города!", Toast.LENGTH_SHORT).show()
            else{
                if (viewModel.myDbManager.checkCityExists(cityPerem.capitalize())) Toast.makeText(this, "Такой город уже существует в списке!", Toast.LENGTH_SHORT).show()
                else request(cityPerem)
            }

        }



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

        viewModel.myDbManager.insertToDbCurData(item)

        val editIntent = Intent().apply{
            putExtra("weather", item)
        }
        setResult(RESULT_OK, editIntent)


        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()


    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        viewModel.myDbManager.closeDb()
        finish()
    }

}