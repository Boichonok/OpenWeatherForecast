package com.example.myapplication.Model.Repository.OpenWeatherRestApiSource

import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord

interface IOpenWeatherRestApiSource {

    suspend fun getCurrentWeatherByCityName(units: String, lang: String,cityName: String): CityCurrentWeatherTable

    suspend fun getCurrentWeatherByCityId(units: String, lang: String,id: String) : CityCurrentWeatherTable
    suspend fun getCurrentWeatherByGeoLocation(units: String, lang: String,coord: Coord): CityCurrentWeatherTable
    suspend fun getCurrentWeatherByZipCode(units: String, lang: String,zipCode: String): CityCurrentWeatherTable

    suspend fun getThreeHourForecastByCityName(units: String, lang: String,cityName: String) : ThreeHourForecastTable
    suspend fun getThreeHourForecastByCityId(units: String, lang: String,id: String) : ThreeHourForecastTable
    suspend fun getThreeHourForecastByGeoLocation(units: String, lang: String,lat: Double, lon: Double): ThreeHourForecastTable
    suspend fun getThreeHourForecastByZipCode(units: String, lang: String,zipCode: String): ThreeHourForecastTable

}