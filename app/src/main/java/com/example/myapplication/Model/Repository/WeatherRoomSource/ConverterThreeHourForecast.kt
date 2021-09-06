package com.example.myapplication.Model.Repository.WeatherRoomSource

import androidx.room.TypeConverter
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast.ThreeHourWeather
import com.google.gson.Gson
import java.util.*

class ConverterThreeHourForecast {

    @TypeConverter
    fun convertListInfoPerDayToString(linkedList: LinkedList<ThreeHourWeather>): String? {
        val gson = Gson()
        return gson.toJson(linkedList)
    }

    @TypeConverter
    fun convertStringToListInfoPerDay(value: String): LinkedList<ThreeHourWeather> {
        val linkedList = LinkedList<ThreeHourWeather>()
        linkedList.addAll(
            Gson().fromJson(value, Array<ThreeHourWeather>::class.java).toList()
        )
        return linkedList
    }

}