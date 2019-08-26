package com.example.myapplication.Model.UseCases.MyWeatherForecast

import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import io.reactivex.Observer
import io.reactivex.observers.DisposableObserver

interface IWeatherForecastManager {
    fun subscribeToUpdateCurrentForecastByLocation(observer: Observer<CityCurrentWeatherTable>)

    fun setSubscriberToUpdateMyCity(observer: DisposableObserver<CityCurrentWeatherTable>)

    fun addMyCityWithCurrentDayForecast(cityName: String)

    fun getAllMyCitiesForecasts(observer: DisposableObserver<List<CityCurrentWeatherTable>>)

    fun startSearchLocation()

    fun stopSearchLocation()

    fun unsubscribeAll()


}