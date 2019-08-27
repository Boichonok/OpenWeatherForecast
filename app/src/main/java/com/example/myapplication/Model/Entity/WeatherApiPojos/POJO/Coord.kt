package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO

import com.google.gson.annotations.SerializedName
import java.io.Serializable

@SuppressWarnings("serial")
data class Coord (@SerializedName("lon") var lon:Double = 0.0,
                  @SerializedName("lat") var lat: Double = 0.0): Serializable

