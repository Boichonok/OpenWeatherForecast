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
import javax.inject.Inject

class WeatherForecastViewModel : ViewModel, IWeatherForecastViewModel {


    private var currentCityForecast = MutableLiveData<CityCurrentWeatherTable>()

    private var myCitiesList = MutableLiveData<List<CityCurrentWeatherTable>>()

    private var internetConnectionState = MutableLiveData<Boolean>()


    private var errors = MutableLiveData<String>()
    private var error = ""

    @Inject
    lateinit var weatherForecastManager: IWeatherForecastManager

    constructor() {
        WeatherForecastApplication.getViewModelComponent().inject(this)
        weatherForecastManager.subscribeToUpdateCurrentForecastByLocation(
            observerCurrentForecastIsLocationProvidersOnline
        )
        weatherForecastManager.getAllMyCitiesForecasts(getAllMyCitiesObserver)
        weatherForecastManager.subscribeToErrorHandler(observerErrors)
        weatherForecastManager.subscribeToObserveInternetStateConnection(observerInternetConnectionStates)
    }

    private var observerInternetConnectionStates = object : Observer<Boolean>
    {
        override fun onComplete() {
        }

        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(t: Boolean?) {
            internetConnectionState.value = t!!
            if(!t)
            {
                errors.value = "Offline mode!"
            }
        }

        override fun onError(e: Throwable?) {
        }

    }

    private var observerErrors = object : Observer<String> {
        override fun onComplete() {
        }

        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(t: String?) {
                errors.value = t!!
            Log.d("ViewModel", "Error: " + t)
        }

        override fun onError(e: Throwable?) {
        }

    }

    private var observerCurrentForecastIsLocationProvidersOnline = object : Observer<CityCurrentWeatherTable> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onComplete() {
        }

        override fun onNext(t: CityCurrentWeatherTable?) {
            Log.d("UseCase", "currentLocationCurrentForecastObserver: onNext(): " + t!!.city_name)
            currentCityForecast.value = t!!

        }

        override fun onError(e: Throwable?) {
        }

    }

    private var addingMyCityObserver = object : Observer<CityCurrentWeatherTable> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onComplete() {
            weatherForecastManager.getAllMyCitiesForecasts(getAllMyCitiesObserver)
            Log.d("UseCase", "Added")
        }

        override fun onNext(t: CityCurrentWeatherTable?) {

        }

        override fun onError(e: Throwable?) {
        }

    }

    private var getAllMyCitiesObserver = object : Observer<List<CityCurrentWeatherTable>> {
        override fun onSubscribe(d: Disposable?) {

        }

        override fun onComplete() {

        }

        override fun onNext(t: List<CityCurrentWeatherTable>?) {

            myCitiesList.value = t!!
            Log.d("UseCase", "List in VIew Model: " + t!!.size)
        }

        override fun onError(e: Throwable?) {
        }

    }


    override fun addMyCity(cityName: String) {
        weatherForecastManager.addMyCityWithCurrentDayForecast(cityName, addingMyCityObserver)
    }

    override fun getWeatherForecastByCurrentLocation(): LiveData<CityCurrentWeatherTable> {
        return currentCityForecast
    }

    override fun getMyCitiesList(): LiveData<List<CityCurrentWeatherTable>> {
        return myCitiesList
    }

    override fun getErrors(): LiveData<String> {
        errors.value = error
        return errors
    }

    override fun startSearchLocation() {
        weatherForecastManager.startSearchLocation()
    }

    override fun stopSearchLocation() {
        weatherForecastManager.stopSearchLocation()
    }

    override fun deleteCityById(id: Int) {
        weatherForecastManager.deleteMyCityByID(id)
    }

    override fun onCleared() {
        super.onCleared()
        weatherForecastManager.unsubscribeAll()
    }

    override fun observeInternetConnectionState(): LiveData<Boolean> {
        return internetConnectionState
    }
}