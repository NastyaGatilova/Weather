package com.example.weather.db

import android.provider.BaseColumns

object MyDbClass {

    const val TABLE_NAME = "forecast"
    const val COLUMN_NAME_CITY = "city"
    const val COLUMN_NAME_TEMPER = "temper"
    const val COLUMN_NAME_DAY_WEEK = "day_week"
    const val COLUMN_NAME_MIN_MAX_TEMP = "min_max_temp"
    const val COLUMN_NAME_IMGURL = "imgurl"

    const val DATABASE_VERSION = 2
    const val DATABASE_NAME = "weather.db"


    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "$COLUMN_NAME_CITY TEXT," +
            "$COLUMN_NAME_TEMPER TEXT, " +
            "$COLUMN_NAME_DAY_WEEK TEXT," +
            "$COLUMN_NAME_MIN_MAX_TEMP TEXT, " +
            "$COLUMN_NAME_IMGURL TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"



}

