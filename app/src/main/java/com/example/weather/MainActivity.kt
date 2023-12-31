package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ActivityMainBinding



open class MainActivity : AppCompatActivity(), RecyclerViewItemClickListener.OnItemClickListener {


    private val weatherAdapter = WeatherAdapter()

    private lateinit var binding: ActivityMainBinding

    val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java)}

    var flagIsConected = true
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        initRcView()


        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        flagIsConected =  networkInfo != null && networkInfo.isConnected

           if (flagIsConected==false){
               Toast.makeText(this, R.string.no_access, Toast.LENGTH_SHORT).show()
           }






        viewModel.getlistUpdateCity().observe(this) { newWeatherList ->
            weatherList.clear()
            weatherList.addAll(newWeatherList)
            weatherAdapter.notifyDataSetChanged()

        }

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



    fun newItemList(view: View?) {


        val intent = Intent(this, ChoiseCityActivity::class.java)
        startActivity(intent)


        finish()

    }


    @RequiresApi(Build.VERSION_CODES.O)
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
