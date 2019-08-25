package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast

import com.google.gson.annotations.SerializedName
import java.util.*


data class ThreeHourForecast (@SerializedName("cod") var cod:String = "",
                              @SerializedName("message") var message: Double = .0,
                              @SerializedName("cnt") var cnt: Int = 0,
                              @SerializedName("city") var city: City? = null,
                              @SerializedName("list") var list: LinkedList<ThreeHourWeather>? = null)
