package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.db.MyDbManager



const val API_key = "1bb93494997fe83bb6d678b29f57d199"


open class MainActivity : AppCompatActivity(), RecyclerViewItemClickListener.OnItemClickListener {
   val myDbManager = MyDbManager(this)

    private val weatherAdapter = WeatherAdapter()
    private var editLauncher: ActivityResultLauncher<Intent>? = null

    private lateinit var binding: ActivityMainBinding

    private val viewModel by lazy { ViewModelProvider(this).get(DataViewModel::class.java)}

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        initRcView()


        val readCity = viewModel.startDb(myDbManager )


        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {

            if (readCity.isNotEmpty()) {
                viewModel.requestForUpdate(readCity[0], readCity, this,sharedPreferences, myDbManager, weatherAdapter )
            }

        } else {
            Toast.makeText(this, "Нет доступа к интернету!", Toast.LENGTH_SHORT).show()
            weatherList.clear()
            weatherList.addAll( viewModel.readListFromDb(myDbManager))
            weatherAdapter.notifyDataSetChanged()
        }



        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                addCity(it.data?.getSerializableExtra("weather") as Weather)
            }

        }

        viewModel.getlistUpdateCity().observe(this, { newWeatherList ->
            weatherList.clear()
            weatherList.addAll(newWeatherList)
            weatherAdapter.notifyDataSetChanged()
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
