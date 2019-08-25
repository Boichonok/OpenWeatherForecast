package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.CurrentWeather

import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.*
import com.google.gson.annotations.SerializedName
import java.util.*

data class CurrentWeather (@SerializedName("coord") var coord: Coord? = null,
                           @SerializedName("weather") var weathers: LinkedList<Weather>? = null,
                           @SerializedName("base") var base: String = "",
                           @SerializedName("main") var main: Main? = null,
                           @SerializedName("wind") var wind: Wind? = null,
                           @SerializedName("clouds") var clouds: Clouds? = null,
                           @SerializedName("dt") var dt: Long = 0,
                           @SerializedName("sys")var sys: Sys? = null,
                           @SerializedName("id") var id: Long = 0,
                           @SerializedName("name") var name: String = "")
