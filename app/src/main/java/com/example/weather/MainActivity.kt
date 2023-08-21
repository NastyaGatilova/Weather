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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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


open class MainActivity : AppCompatActivity(),RecyclerViewItemClickListener.OnItemClickListener {
    val myDbManager = MyDbManager(this)


    private var act_main_rcView: RecyclerView? = null
    private var city_listatem: TextView? = null
    private var temp_listatem: TextView? = null

    private val Wadapter= WeatherAdapter()

    private var editLauncher: ActivityResultLauncher<Intent>? = null




    @SuppressLint("SuspiciousIndentation", "MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDbManager.openDb()


        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                addCity(it.data?.getSerializableExtra("weather") as Weather)
                myDbManager.updateToDbCurData(it.data?.getSerializableExtra("weather") as Weather)
            }

        }

        initWidgets()
        initRcView()

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        Wadapter.notifyDataSetChanged()
        if (myDbManager.readDbCurData().isNotEmpty()){
           searchСity()
        val item = myDbManager.readDbCurData()

        weatherList.clear()
        addCity2(item)

       }

    }

    private fun initWidgets(){
        act_main_rcView = findViewById(R.id.act_main_rcView)
        city_listatem = findViewById(R.id.city_listatem)
        temp_listatem = findViewById(R.id.temp_listatem)
    }

    private fun initRcView(){

        act_main_rcView?.layoutManager = LinearLayoutManager(this@MainActivity) //настройка rcview по вертикали
        act_main_rcView?.adapter = Wadapter
        val itemClickListener = RecyclerViewItemClickListener(this, act_main_rcView!!, this)
        act_main_rcView?.addOnItemTouchListener(itemClickListener)

        val swipeToDeleteCallback = SwipeToDeleteCallback(Wadapter)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(act_main_rcView)
       }


    @SuppressLint("NotifyDataSetChanged")
    fun addCity2(weather: MutableList<Weather>) {
        weatherList.addAll(weather)
        Wadapter.notifyDataSetChanged()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun searchСity(){
    val helpList: MutableList<Weather>
    helpList = myDbManager.readDbCurData()
        if (helpList.isNotEmpty()) {
         for (item in helpList) {
           request(item.city)
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
                parse(result, city)
            },
            {
                    error ->

            }
        )
        queue.add(request)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun parse(result: String, city:String) {




        val mainObject = JSONObject(result)
        val mainn = mainObject.getJSONObject("main")
        val tempTec = mainn.getString("temp").toDouble().roundToInt()
     //   tempPerem =tempTec.toString()

        val icon = mainObject.getJSONArray("weather").getJSONObject(0).getString("icon")

        val image2 =  "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/$icon.png"


        val item = Weather(
            "${city.capitalize()}",
            "$tempTec °C",
            "ВТ",
            "1 / 1",
            "$image2"
        )


        myDbManager.updateToDbCurData(item)



        Wadapter.notifyDataSetChanged()


    }




    private fun addCity(weather: Weather){

        weatherList.add(weather)
        Wadapter.notifyDataSetChanged()

    }

    fun newItemList(view: View?){

        editLauncher?.launch(Intent(this@MainActivity, ChoiseCityActivity::class.java))
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
