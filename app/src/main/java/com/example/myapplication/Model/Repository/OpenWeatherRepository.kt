package com.example.myapplication.Model.Repository

import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.IOpenWeatherRestApiSource
import com.example.myapplication.Model.Repository.WeatherRoomSource.IWeatherRoomSource
import org.koin.core.component.KoinComponent

class OpenWeatherRepository: IOpenWeatherRepository, KoinComponent {
    override val restApiSource: IOpenWeatherRestApiSource = getKoin().get()
    override val roomSource: IWeatherRoomSource = getKoin().get()
}