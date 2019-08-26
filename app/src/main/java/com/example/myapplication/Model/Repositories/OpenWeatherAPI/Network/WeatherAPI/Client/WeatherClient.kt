package com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


class WeatherClient: IWeatherClient {

    companion object {
        const val CURRENT_WEATHER = "/data/2.5/weather"
        const val FORECAST = "/data/2.5/forecast"
        const val API_KEY = "011709f71a27aa6286aa58816ed7a0a6"
    }
    private val BASE_URL = "https://api.openweathermap.org"

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    override fun getWeatherAPIClient(): Retrofit {
        return retrofit
    }


}