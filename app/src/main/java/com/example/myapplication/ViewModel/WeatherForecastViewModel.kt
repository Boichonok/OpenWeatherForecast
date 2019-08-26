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
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WeatherForecastViewModel : ViewModel, IWeatherForecastViewModel {


    private var currentCityForecast = MutableLiveData<CityCurrentWeatherTable>()



    private var errors = MutableLiveData<String>()

    @Inject
    lateinit var weatherForecastManager: IWeatherForecastManager

    constructor() {
        WeatherForecastApplication.getViewModelComponent().inject(this)
        weatherForecastManager.subscribeToUpdateCurrentForecastByLocation(currentLocationCurrentForecastObserver)
        weatherForecastManager.subscribeToUpdateMyCity(addingMyCityObserver)
        weatherForecastManager.getAllMyCitiesForecasts(getAllMyCitiesObserver)
    }

    private var currentLocationCurrentForecastObserver = object : DisposableObserver<CityCurrentWeatherTable>() {
        override fun onComplete() {
            Log.d("ViewModel", "currentLocationCurrentForecastObserver: onComplete")
        }

        override fun onNext(t: CityCurrentWeatherTable?) {
            Log.d("ViewModel", "currentLocationCurrentForecastObserver: onNext() current forecast: " + t!!.city_name + " " + t.temperature)
            currentCityForecast.value = t
            Log.d("ViewModel", "currentLocationCurrentForecastObserver: onNext() current forecast MLiveData: " + currentCityForecast.value!!.city_name + " " + currentCityForecast.value!!.temperature)

        }

        override fun onError(e: Throwable?) {
            Log.d("ViewModel","currentLocationCurrentForecastObserver: onError(): " + e!!.message)
            errors.value = e!!.message
        }

    }

    private var addingMyCityObserver  = object : DisposableObserver<CityCurrentWeatherTable>()
    {
        override fun onComplete() {
            weatherForecastManager.getAllMyCitiesForecasts(getAllMyCitiesObserver)
        }

        override fun onNext(t: CityCurrentWeatherTable?) {

        }

        override fun onError(e: Throwable?) {
            errors.value = e!!.message
        }

    }

    private var getAllMyCitiesObserver = object : DisposableObserver<List<CityCurrentWeatherTable>>()
    {
        override fun onComplete() {

        }

        override fun onNext(t: List<CityCurrentWeatherTable>?) {

        }

        override fun onError(e: Throwable?) {

        }

    }


    override fun addMyCity(cityName: String) {
        weatherForecastManager.addMyCityWithCurrentDayForecast(cityName)
    }

    override fun getWeatherForecastByCurrentLocation(): LiveData<CityCurrentWeatherTable> {
        return currentCityForecast
    }

    override fun getMyCitiesListAdapter() {

    }

    override fun startSearchLocation() {
        weatherForecastManager.startSearchLocation()
    }

    override fun stopSearchLocation() {
        weatherForecastManager.stopSearchLocation()
    }

    override fun onCleared() {
        super.onCleared()
        weatherForecastManager.unsubscribeAll()
        currentLocationCurrentForecastObserver.dispose()
    }
}