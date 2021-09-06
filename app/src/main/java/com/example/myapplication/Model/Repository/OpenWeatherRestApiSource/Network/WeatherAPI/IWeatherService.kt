package com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI

import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.CurrentWeather.CurrentWeather
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast.ThreeHourForecast
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.Client.WeatherClient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface IWeatherService {

    @GET(WeatherClient.CURRENT_WEATHER)
    suspend fun getCurrentWeatherByCityName(@QueryMap options: Map<String, String>): Response<CurrentWeather>

    @GET(WeatherClient.CURRENT_WEATHER)
    suspend fun getCurrentWeatherByCityID(@QueryMap options: Map<String, String>): Response<CurrentWeather>

    @GET(WeatherClient.CURRENT_WEATHER)
    suspend fun getCurrentWeatherByGeoCoordinates(@QueryMap options: Map<String, String>): Response<CurrentWeather>

    @GET(WeatherClient.CURRENT_WEATHER)
    suspend fun getCurrentWeatherByZipCode(@QueryMap options: Map<String, String>): Response<CurrentWeather>


    @GET(WeatherClient.FORECAST)
    suspend fun getThreeHourForecastByCityName(@QueryMap options: Map<String, String>): Response<ThreeHourForecast>

    @GET(WeatherClient.FORECAST)
    suspend fun getThreeHourForecastByCityID(@QueryMap options: Map<String, String>): Response<ThreeHourForecast>

    @GET(WeatherClient.FORECAST)
    suspend fun getThreeHourForecastByGeoCoordinates(@QueryMap options: Map<String, String>): Response<ThreeHourForecast>

    @GET(WeatherClient.FORECAST)
    suspend fun getThreeHourForecastByZipCode(@QueryMap options: Map<String, String>): Response<ThreeHourForecast>
}