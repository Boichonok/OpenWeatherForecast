package com.example.myapplication.Model.Repositories.OpenWeatherAPI

import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.CurrentWeather.CurrentWeather
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast.ThreeHourForecast
import io.reactivex.Completable
import io.reactivex.Flowable
import retrofit2.Call

interface IOpenWeatherAPIManager {

    fun getCurrentWeatherByCityName(units: String, lang: String,cityName: String): Flowable<CurrentWeather>
    fun getCurrentWeatherByCityId(units: String, lang: String,id: String) : Flowable<CurrentWeather>
    fun getCurrentWeatherByGeoLocation(units: String, lang: String,coord: Coord): Flowable<CurrentWeather>
    fun getCurrentWeatherByZipCode(units: String, lang: String,zipCode: String): Flowable<CurrentWeather>

    fun getThreeHourForecastByCityName(units: String, lang: String,cityName: String) : Flowable<ThreeHourForecast>
    fun getThreeHourForecastByCityId(units: String, lang: String,id: String) : Flowable<ThreeHourForecast>
    fun getThreeHourForecastByGeoLocation(units: String, lang: String,lat: Double, lon: Double): Flowable<ThreeHourForecast>
    fun getThreeHourForecastByZipCode(units: String, lang: String,zipCode: String): Flowable<ThreeHourForecast>

}