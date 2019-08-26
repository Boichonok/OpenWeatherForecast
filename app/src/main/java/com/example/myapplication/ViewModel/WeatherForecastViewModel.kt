package com.example.myapplication.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastManager
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import javax.inject.Inject

class WeatherForecastViewModel : ViewModel, IWeatherForecastViewModel {


    private var currentCityForecast = MutableLiveData<CityCurrentWeatherTable>()



    private var errors = MutableLiveData<String>()

    @Inject
    lateinit var weatherForecastManager: IWeatherForecastManager

    constructor() {
        WeatherForecastApplication.getViewModelComponent().inject(this)
        weatherForecastManager.subscribeToUpdateCurrentForecastByLocation(currentLocationCurrentForecastObserver)
        weatherForecastManager.setSubscriberToUpdateMyCity(addingMyCityObserver)
        weatherForecastManager.getAllMyCitiesForecasts(getAllMyCitiesObserver)
    }

    private var currentLocationCurrentForecastObserver = object : Observer<CityCurrentWeatherTable> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onComplete() {
        }

        override fun onNext(t: CityCurrentWeatherTable?) {
            currentCityForecast.value = t

        }

        override fun onError(e: Throwable?) {
            Log.d("UseCase","currentLocationCurrentForecastObserver: onError(): " + e!!.localizedMessage)
//            errors.value = e!!.message
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
            Log.d("ViewModel","AllCities: " + t!!)
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
        addingMyCityObserver.dispose()
        getAllMyCitiesObserver.dispose()
    }
}