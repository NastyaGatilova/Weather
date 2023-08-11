package com.example.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.db.MyDbManager

const val API_key="1bb93494997fe83bb6d678b29f57d199"

val adapter= WeatherAdapter()
val adapterDay= DayWeatherAdapter()
var deleteCity = ""

var isFirstOpen = 0
open class MainActivity : AppCompatActivity() {
    val myDbManager = MyDbManager(this)
     var act_main_rcView: RecyclerView? = null
    var city_listatem: TextView? = null
    var temp_listatem: TextView? = null


    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isFirstOpen++

        act_main_rcView = findViewById(R.id.act_main_rcView)
        city_listatem = findViewById(R.id.city_listatem)
        temp_listatem = findViewById(R.id.temp_listatem)

        myDbManager.openDb()
        initRcView()

        val swipeToDeleteCallback = SwipeToDeleteCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(act_main_rcView)


        myDbManager.readDbCurData()



    }



    private fun initRcView(){
        act_main_rcView?.layoutManager = LinearLayoutManager(this@MainActivity) //настройка rcview по вертикали
        act_main_rcView?.adapter = adapter

       }

    fun newItemList(view: View?){

        val newNoteIntent = Intent(this, ChoiseCityActivity::class.java)
        startActivity(newNoteIntent)
        finish()

    }





}