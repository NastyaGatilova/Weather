package com.example.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.weather.databinding.ActivityChoiseCityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

            if (cityPerem == "") Toast.makeText(this, R.string.enter_city, Toast.LENGTH_SHORT).show()
            else if (flagIsConected==true){
                if (viewModel.myDbManager.checkCityExists(cityPerem.capitalize())) Toast.makeText(this, R.string.city_exists, Toast.LENGTH_SHORT).show()
                else request(cityPerem)
            }
            else Toast.makeText(this, R.string.no_access, Toast.LENGTH_SHORT).show()
        }
    }




    fun request(city: String) {

       CoroutineScope(Dispatchers.IO).launch {


           try {
               val weather = service.getWeather(city, API_key, "metric")
               val tempTec = weather?.main?.temp?.roundToInt().toString()
               tempPerem = tempTec

               val img = weather?.weather?.get(0)?.icon?.let { icon ->
                   "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"
               } ?: ""


               val item = Weather(
                   "${cityPerem.capitalize()}",
                   "$tempTec°C",
                   "",
                   "",
                   "${img}"
               )

               withContext(Dispatchers.Main.immediate) {viewModel.myDbManager.insertToDbCurData(item)}



               val intent = Intent(this@ChoiseCityActivity, MainActivity::class.java)
               startActivity(intent)
               finish()
           }
           catch (e: Exception)  {


               withContext(Dispatchers.Main.immediate){ Toast.makeText(this@ChoiseCityActivity, R.string.no_city, Toast.LENGTH_SHORT).show()
                       Toast.makeText(this@ChoiseCityActivity, R.string.if_problem, Toast.LENGTH_LONG).show()
                   }


       }
            }}



    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        viewModel.myDbManager.closeDb()
        finish()
    }

}