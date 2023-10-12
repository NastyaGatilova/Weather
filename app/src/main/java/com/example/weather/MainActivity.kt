package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date


open class MainActivity : AppCompatActivity(), RecyclerViewItemClickListener.OnItemClickListener {


    private val weatherAdapter = WeatherAdapter()
    private var editLauncher: ActivityResultLauncher<Intent>? = null

    private lateinit var binding: ActivityMainBinding

    val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java)}

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initRcView()




        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                addCity(it.data?.getSerializableExtra("weather") as Weather)
            }

        }

        viewModel.getlistUpdateCity().observe(this, { newWeatherList ->
            weatherList.clear()
            weatherList.addAll(newWeatherList)
            runOnUiThread { weatherAdapter.notifyDataSetChanged() }


        })

    }





    private fun initRcView() {

        binding.rcView.layoutManager =
            LinearLayoutManager(this@MainActivity)
        binding.rcView.adapter = weatherAdapter
        val itemClickListener = RecyclerViewItemClickListener(this, binding.rcView, this)
        binding.rcView.addOnItemTouchListener(itemClickListener)

        val swipeToDeleteCallback = SwipeToDeleteCallback(weatherAdapter)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.rcView)
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun addCity(weather: Weather) {
        weatherList.add(weather)
        weatherAdapter.notifyDataSetChanged()
    }

    fun newItemList(view: View?) {

        editLauncher?.launch(Intent(this@MainActivity, ChoiseCityActivity::class.java))
        finish()

    }


    override fun onItemClick(view: View, position: Int) {

        val intent = Intent(this, DataDetailActivity::class.java)

        intent.putExtra("city", weatherList[position].city)
        intent.putExtra("temp", weatherList[position].temp)
        intent.putExtra("imageurl", weatherList[position].imageurl)


        startActivity(intent)

    }


}

class RecyclerViewItemClickListener(
    context: Context,
    recyclerView: RecyclerView,
    private val mListener: OnItemClickListener?
) :
    RecyclerView.OnItemTouchListener {

    private val mGestureDetector: GestureDetector

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    init {
        mGestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
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
