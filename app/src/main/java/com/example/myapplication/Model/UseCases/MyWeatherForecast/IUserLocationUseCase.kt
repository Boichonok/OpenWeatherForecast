package com.example.myapplication.Model.UseCases.MyWeatherForecast

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.SharedFlow

interface IUserLocationUseCase {
    val userLocation: SharedFlow<LatLng>
    fun currUserLocationRequest()
}