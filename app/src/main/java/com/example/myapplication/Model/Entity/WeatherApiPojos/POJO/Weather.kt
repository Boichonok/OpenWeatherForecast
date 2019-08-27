package com.example.myapplication.Model.Entity.WeatherApiPojos.POJO

import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@SuppressWarnings("serial")
data class Weather (@NonNull @SerializedName("id") var id: Long = 0,
                    @NonNull @SerializedName("main") var main: String = "",
                    @NonNull @SerializedName("description") var description: String = "",
                    @NonNull @SerializedName("icon") var icon: String = ""): Serializable

