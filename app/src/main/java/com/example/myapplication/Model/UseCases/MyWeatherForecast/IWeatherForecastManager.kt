package com.example.myapplication.Model.UseCases.MyWeatherForecast

import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import io.reactivex.Completable
import io.reactivex.Observer
import io.reactivex.observers.DisposableObserver

interface IWeatherForecastManager {

    fun subscribeToErrorHandler(observer: Observer<String>)

    fun subscribeToUpdateCurrentForecastByLocation(observer: Observer<CityCurrentWeatherTable>)

    fun addMyCityWithCurrentDayForecast(cityName: String,observer: Observer<CityCurrentWeatherTable>)

    fun getAllMyCitiesForecasts(observer: Observer<List<CityCurrentWeatherTable>>)

    fun startSearchLocation()

    fun stopSearchLocation()

    fun unsubscribeAll()

    fun deleteMyCityByID(id: Int): Boolean

    fun subscribeToObserveInternetStateConnection(observer: Observer<Boolean>)

}