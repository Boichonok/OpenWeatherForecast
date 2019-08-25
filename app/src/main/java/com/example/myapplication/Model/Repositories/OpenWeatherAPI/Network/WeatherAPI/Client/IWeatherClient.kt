package com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client

import retrofit2.Retrofit

interface IWeatherClient {
    fun getWeatherAPIClient():Retrofit
}