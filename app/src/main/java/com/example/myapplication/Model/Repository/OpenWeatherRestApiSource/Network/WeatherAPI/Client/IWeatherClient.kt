package com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.Client

import retrofit2.Retrofit

interface IWeatherClient {
    fun getWeatherAPIClient():Retrofit
}