package com.example.myapplication.Model.Repository

import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.IOpenWeatherRestApiSource
import com.example.myapplication.Model.Repository.WeatherRoomSource.IWeatherRoomSource

interface IOpenWeatherRepository {
    val restApiSource: IOpenWeatherRestApiSource
    val roomSource: IWeatherRoomSource
}