package com.example.myapplication.Model.UseCases.MyWeatherForecast.Interactors

import android.location.Location
import androidx.annotation.RequiresPermission
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IUserLocationUseCase
import com.example.myapplication.Model.asyncScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class UserLocationInteraction(
    private val fusedLocationProviderClient: FusedLocationProviderClient
): IUserLocationUseCase {

    private val _userLocation = MutableSharedFlow<LatLng>()

    override val userLocation: SharedFlow<LatLng> = _userLocation

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun currUserLocationRequest() {
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token).addOnSuccessListener { userLocation ->
            asyncScope.launch {
                if(userLocation != null) {
                    _userLocation.emit(userLocation.toLatLng())
                }
            }
        }
    }
}

fun Location.toLatLng() = LatLng(latitude,longitude)