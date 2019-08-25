package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO

import com.google.gson.annotations.SerializedName

data class Sys (@SerializedName("message") var message: Double = 0.0,
                @SerializedName("country") var country: String = "",
                @SerializedName("sunrise") var sunrise: Long = 0,
                @SerializedName("sunset") var sunset: Long = 0,
                @SerializedName("pod") var  pod: String = "")

