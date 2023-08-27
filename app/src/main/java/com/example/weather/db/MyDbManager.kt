package com.example.weather.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.weather.Weather
import kotlin.math.roundToInt

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null


    fun openDb() {
        db = myDbHelper.writableDatabase
    }


    fun insertToDbCurData(weather: Weather) {

        val values = ContentValues().apply {
            put(MyDbClass.COLUMN_NAME_CITY, weather.city)
            put(MyDbClass.COLUMN_NAME_TEMPER, weather.temp)
            put(MyDbClass.COLUMN_NAME_IMGURL, weather.imageurl)


        }
        db?.insert(MyDbClass.TABLE_NAME, null, values)
    }


    @SuppressLint("Range")
    fun readDbCurData(): MutableList<Weather> {

        val dataList = mutableListOf<Weather>()

        val cursor = db?.rawQuery(
            "SELECT ${MyDbClass.COLUMN_NAME_CITY} , ${MyDbClass.COLUMN_NAME_TEMPER}, ${MyDbClass.COLUMN_NAME_IMGURL} FROM ${MyDbClass.TABLE_NAME}",
            null
        )


        while (cursor?.moveToNext()!!) {
            val city = cursor?.getString(cursor.getColumnIndex(MyDbClass.COLUMN_NAME_CITY))
            val temperature =
                cursor.getFloat(cursor.getColumnIndex(MyDbClass.COLUMN_NAME_TEMPER)).toDouble()
                    .roundToInt().toString()
            val img = cursor?.getString(cursor.getColumnIndex(MyDbClass.COLUMN_NAME_IMGURL))


            val item = Weather(
                "$city",
                "$temperature °C",
                "",
                "",
                "$img"
            )


            dataList.add(item)


        }

        cursor.close()
        return dataList
    }


    fun updateTable(temp: String, img: String, city: String){
        val db = myDbHelper.writableDatabase
        val values = ContentValues().apply {


            put(MyDbClass.COLUMN_NAME_TEMPER, temp)
            put(MyDbClass.COLUMN_NAME_IMGURL, img)


        }
        db?.update(
            MyDbClass.TABLE_NAME,
            values,
            "${MyDbClass.COLUMN_NAME_CITY}=?",
            arrayOf(city)
        )


    }
    @SuppressLint("Range")
    fun readTable():MutableList<String> {

        val cityList = mutableListOf<String>()

        val cursor2 = db?.rawQuery(
            "SELECT ${MyDbClass.COLUMN_NAME_CITY} FROM ${MyDbClass.TABLE_NAME}",
            null
        )


        while (cursor2?.moveToNext()!!) {
            val city = cursor2?.getString(cursor2.getColumnIndex(MyDbClass.COLUMN_NAME_CITY))
            cityList.add(city.toString())
        }

        cursor2.close()
        return cityList

    }

    fun deleteFromTable() {
        db?.execSQL("DELETE FROM ${MyDbClass.TABLE_NAME}")
    }

    fun deleteCity(cityName: String) {

        val db = myDbHelper.writableDatabase
        val selection = "${MyDbClass.COLUMN_NAME_CITY} = ?"
        val selectionArgs = arrayOf(cityName)
        db.delete(MyDbClass.TABLE_NAME, selection, selectionArgs)
    }

    fun checkCityExists(city: String): Boolean {
        val db = myDbHelper.writableDatabase
        val query = "SELECT * FROM ${MyDbClass.TABLE_NAME} WHERE ${MyDbClass.COLUMN_NAME_CITY} = ?"
        val cursor = db.rawQuery(query, arrayOf(city))
        val exist = cursor.count > 0
        cursor.close()
        return exist
    }

    fun closeDb() {
        myDbHelper.close()
    }

}