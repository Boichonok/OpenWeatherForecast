package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast

import com.google.gson.annotations.SerializedName

data class Snow (@SerializedName("3h") override var threeHour: Double = 0.0): IPrecipitation

