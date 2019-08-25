package com.example.myapplication.Model.UseCases.MyWeatherForecast

import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import io.reactivex.Flowable
import io.reactivex.Observer
import io.reactivex.Single

interface IWeatherForecastManager {


    fun subscribeToUpdateCurrentForecastByLocation(observer: Observer<CityCurrentWeatherTable>)

    fun addMyCityWithCurrentDayForecast(cityName: String): Single<CityCurrentWeatherTable>

    fun addMyCityWithFiveDayForecast(cityName: String): Single<ThreeHourForecastTable>

    fun startSearchLocation()

    fun stopSearchLocation()

    fun unsubscribeAll()


}