package com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI

import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.CurrentWeather.CurrentWeather
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast.ThreeHourForecast
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface IWeatherService {

    @GET(WeatherClient.CURRENT_WEATHER)
    fun getCurrentWeatherByCityName(@QueryMap options: Map<String, String> ): Flowable<CurrentWeather>

    @GET(WeatherClient.CURRENT_WEATHER)
    fun  getCurrentWeatherByCityID(@QueryMap options: Map<String, String> ): Flowable<CurrentWeather>

    @GET(WeatherClient.CURRENT_WEATHER)
    fun getCurrentWeatherByGeoCoordinates(@QueryMap options: Map<String, String>): Flowable<CurrentWeather>

    @GET(WeatherClient.CURRENT_WEATHER)
    fun getCurrentWeatherByZipCode(@QueryMap options: Map<String, String> ): Flowable<CurrentWeather>


    @GET(WeatherClient.FORECAST)
    fun getThreeHourForecastByCityName(@QueryMap options: Map<String, String> ): Flowable<ThreeHourForecast>

    @GET(WeatherClient.FORECAST)
    fun getThreeHourForecastByCityID(@QueryMap options: Map<String, String> ): Flowable<ThreeHourForecast>

    @GET(WeatherClient.FORECAST)
    fun getThreeHourForecastByGeoCoordinates(@QueryMap options: Map<String, String> ): Flowable<ThreeHourForecast>

    @GET(WeatherClient.FORECAST)
    fun getThreeHourForecastByZipCode(@QueryMap options: Map<String, String> ): Flowable<ThreeHourForecast>
}