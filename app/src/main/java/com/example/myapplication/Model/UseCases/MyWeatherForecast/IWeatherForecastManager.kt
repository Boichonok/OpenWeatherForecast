package com.example.myapplication.Model.UseCases.MyWeatherForecast

import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import io.reactivex.Single
import io.reactivex.observers.DisposableObserver

interface IWeatherForecastManager {


    fun subscribeToUpdateCurrentForecastByLocation(observer: DisposableObserver<CityCurrentWeatherTable>)

    fun addMyCityWithCurrentDayForecast(cityName: String): Single<CityCurrentWeatherTable>

    fun addMyCityWithFiveDayForecast(cityName: String): Single<ThreeHourForecastTable>

    fun startSearchLocation()

    fun stopSearchLocation()

    fun unsubscribeAll()


}