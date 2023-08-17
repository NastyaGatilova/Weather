package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.db.MyDbManager
import org.json.JSONObject
import kotlin.math.roundToInt


const val API_key="1bb93494997fe83bb6d678b29f57d199"

var adapter= WeatherAdapter()
val adapterDay= DayWeatherAdapter()



var isFirstOpen = 0
open class MainActivity : AppCompatActivity(),RecyclerViewItemClickListener.OnItemClickListener {
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
        initRcView()

        myDbManager.openDb()


        val swipeToDeleteCallback = SwipeToDeleteCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(act_main_rcView)


        searchСity()
        val item = myDbManager.readDbCurData()

           if (isFirstOpen == 1) {
                //adapter.addCity2(item)
                addCity2(item)
           }


    }

    fun addCity2(weather: ArrayList<Weather>) {
        weatherList.addAll(weather)
        adapter.notifyDataSetChanged()
    }

    private fun initRcView(){
        act_main_rcView?.layoutManager = LinearLayoutManager(this@MainActivity) //настройка rcview по вертикали

        act_main_rcView?.adapter = adapter

        val itemClickListener = RecyclerViewItemClickListener(this, act_main_rcView!!, this)
        act_main_rcView?.addOnItemTouchListener(itemClickListener)

       }


private fun searchСity(){

    var helpList: ArrayList<Weather>
    helpList = myDbManager.readDbCurData()

     if (helpList.isNotEmpty()) {
         for (item in helpList) {
           request(item.city)
        }
     }

    Log.d("--Wlist", "${helpList}")


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
                  //  Log.d("--Wlist", "Результат:  $result")
                parse(result, city)
            },
            {
                    error ->

            }
        )
        queue.add(request)

    }

    private fun parse(result: String, city:String) {
        val mainObject = JSONObject(result)

        val mainn = mainObject.getJSONObject("main")
        val tempTec = mainn.getString("temp").toDouble().roundToInt()
        tempPerem =tempTec.toString()

        val icon = mainObject.getJSONArray("weather").getJSONObject(0).getString("icon")

        var image2 =  "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"

        Log.d("--Wlist", "Погода1:  $city, $tempPerem,  $image2")

        myDbManager.updateToDbCurData(tempPerem,image2,city)

        Log.d("--Wlist", "Погода2:  $city, $tempPerem,  $image2")

        adapter.notifyDataSetChanged()


    }


    fun newItemList(view: View?){

        val newNoteIntent = Intent(this, ChoiseCityActivity::class.java)
        startActivity(newNoteIntent)
        finish()

    }


    override fun onItemClick(view: View, position: Int) {
        helpWeather = weatherList[position]
        val intent = Intent(this, DataDetailActivity::class.java)
        startActivity(intent)
    }


}


class RecyclerViewItemClickListener(context: Context, recyclerView: RecyclerView, private val mListener: OnItemClickListener?) :
    RecyclerView.OnItemTouchListener {

    private val mGestureDetector: GestureDetector

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    init {
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
