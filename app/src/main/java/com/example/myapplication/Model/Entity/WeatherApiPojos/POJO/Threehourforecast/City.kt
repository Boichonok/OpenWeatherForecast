package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast

import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.google.gson.annotations.SerializedName

data class City (@SerializedName("id") var id: Long = 0,
                 @SerializedName("name") var name: String = "",
                 @SerializedName("coord") var coord: Coord? = null,
                 @SerializedName("country") var country: String = "",
                 @SerializedName("population") var population: Long = 0)
