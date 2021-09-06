package com.example.myapplication.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.UseCases.MyWeatherForecast.INetworkConnectionUseCase
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IUserLocationUseCase
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class WeatherForecastViewModel(
    private val weatherForecastUseCase: IWeatherForecastUseCases,
    networkConnectionUseCases: INetworkConnectionUseCase,
    private val userLocationUseCase: IUserLocationUseCase
) : ViewModel(), IWeatherForecastViewModel {


    override val currentCityForecast = weatherForecastUseCase.currentCityWeatherForecast.asLiveData()

    override val myCitiesList = weatherForecastUseCase.getAllMySavedCitiesForecast().asLiveData()

    override val isInternetAvl = networkConnectionUseCases.networkAvlChanges.asLiveData()

    override val errors = MutableLiveData<String>()

    private var currUserLocationJob: Job? = null

    override fun addMyCity(cityName: String) {
        viewModelScope.launch {
            weatherForecastUseCase.saveMyCityWeatherForecast(cityName)
        }
    }

    override fun startSearchLocation() {
        userLocationUseCase.currUserLocationRequest()
        currUserLocationJob = viewModelScope.launch {
            userLocationUseCase.userLocation.collectLatest { userLocation ->
                weatherForecastUseCase.setCurrCityLocation(
                    IWeatherForecastUseCases.CityLocation(userLocation)
                )
            }
        }
    }

    override fun stopSearchLocation() {
        currUserLocationJob?.cancel()
        currUserLocationJob = null
    }

    override fun deleteCityById(id: Int) {
        viewModelScope.launch {
            weatherForecastUseCase.deleteMyCityWeatherForecastById(id)
        }
    }
}