package com.example.myapplication.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastManager
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WeatherForecastViewModel : ViewModel, IWeatherForecastViewModel {



    private var currentCityForecast: MutableLiveData<CityCurrentWeatherTable> = MutableLiveData<CityCurrentWeatherTable>()

    override fun startSearchLocation() {
        weatherForecastManager.startSearchLocation()
    }

    override fun stopSearchLocation() {
        weatherForecastManager.stopSearchLocation()
    }


    @Inject
    lateinit var weatherForecastManager: IWeatherForecastManager

    constructor() {
        WeatherForecastApplication.getViewModelComponent().inject(this)
        weatherForecastManager.subscribeToUpdateCurrentForecastByLocation(currentLocationCurrentForecastObserver)
    }



    private var currentLocationCurrentForecastObserver: Observer<CityCurrentWeatherTable> = object : Observer<CityCurrentWeatherTable> {
        override fun onComplete() {
            Log.d("ViewModel", "onComplete")
        }

        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(t: CityCurrentWeatherTable?) {
            Log.d("ViewModel", "onNext() current forecast: " + t!!.city_name + " " + t.temperature)
            currentCityForecast.value = t
            Log.d("ViewModel", "onNext() current forecast MLiveData: " + currentCityForecast.value!!.city_name + " " + currentCityForecast.value!!.temperature)

        }

        override fun onError(e: Throwable?) {
            Log.d("ViewModel","onError(): " + e!!.message)
        }

    }

    override fun getWeatherForecastByCurrentLocation(): LiveData<CityCurrentWeatherTable> {
        return currentCityForecast
    }

    override fun onCleared() {
        super.onCleared()
        weatherForecastManager.unsubscribeAll()
    }
}