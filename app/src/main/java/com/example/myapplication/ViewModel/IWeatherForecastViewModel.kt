package com.example.myapplication.ViewModel

import android.location.Location
import androidx.lifecycle.LiveData
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

interface IWeatherForecastViewModel {
    val isInternetAvl: LiveData<Boolean>
    val currentCityForecast: LiveData<CityCurrentWeatherTable>
    val myCitiesList: LiveData<List<CityCurrentWeatherTable>>
    val errors: LiveData<String>
    fun addMyCity(cityName: String)
    fun startSearchLocation()
    fun stopSearchLocation()
    fun deleteCityById(id: Int)

}