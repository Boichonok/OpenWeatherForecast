package com.example.myapplication.Model.Repositories.WeatherRoom

import androidx.room.TypeConverter
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast.ThreeHourWeather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ConverterThreeHourForecast {

        @TypeConverter
        fun convertListInfoPerDayToString(linkedList: LinkedList<ThreeHourWeather>): String? {
            val gson = Gson()
            return gson.toJson(linkedList)
        }

        @TypeConverter
        fun convertStringToListInfoPerDay(value: String): LinkedList<ThreeHourWeather> {
            val listType = object : TypeToken<LinkedList<ThreeHourWeather>>() {

            }.type
            return Gson().fromJson(value, listType)
        }

}