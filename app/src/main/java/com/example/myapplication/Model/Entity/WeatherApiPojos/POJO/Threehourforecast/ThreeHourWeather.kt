package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast

import androidx.annotation.NonNull
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.*
import com.google.gson.annotations.SerializedName

data class ThreeHourWeather(@NonNull @SerializedName("dt") var dt: Long = 0,
                            @NonNull @SerializedName("main") var main: Main? = null,
                            @NonNull @SerializedName("weather") var weathers: List<Weather>? = null,
                            @NonNull @SerializedName("clouds") var clouds: Clouds? = null,
                            @NonNull @SerializedName("wind") var wind: Wind? = null,
                            @NonNull @SerializedName("rain") var rain: Rain? = null,
                            @NonNull @SerializedName("snow") var snow: Snow? = null,
                            @NonNull @SerializedName("dt_txt") var dtTxt:String = "")

