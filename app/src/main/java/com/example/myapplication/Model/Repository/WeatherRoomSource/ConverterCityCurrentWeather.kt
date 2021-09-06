package com.example.myapplication.Model.Repository.WeatherRoomSource

import androidx.room.TypeConverter
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ConverterCityCurrentWeather {
    @TypeConverter
    fun convertListWeatherToString(linkedList: LinkedList<Weather>): String? {
        val gson = Gson()
        return gson.toJson(linkedList)
    }

    @TypeConverter
    fun convertStringToListWeather(value: String): LinkedList<Weather> {
        val listType = object : TypeToken<LinkedList<Weather>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

}

