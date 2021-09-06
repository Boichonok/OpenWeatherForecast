package com.example.myapplication.Model.UseCases.MyWeatherForecast

import kotlinx.coroutines.flow.StateFlow

interface INetworkConnectionUseCase {
    val networkAvlChanges: StateFlow<Boolean>
}