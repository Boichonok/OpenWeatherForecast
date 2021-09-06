package com.example.myapplication.Model.UseCases.MyWeatherForecast

import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface IWeatherForecastUseCases {

    val errors: Flow<Throwable>

    val currentCityWeatherForecast: Flow<CityCurrentWeatherTable>

    var anyLocationProviderEnabled: Boolean

    suspend fun saveMyCityWeatherForecast(cityName: String)

    suspend fun deleteMyCityWeatherForecastById(cityId: Int)

    fun getAllMySavedCitiesForecast(): Flow<List<CityCurrentWeatherTable>>

    suspend fun setCurrCityLocation(cityLocation: CityLocation)

    data class CityLocation(val location: LatLng, val cityName: String? = null)
}