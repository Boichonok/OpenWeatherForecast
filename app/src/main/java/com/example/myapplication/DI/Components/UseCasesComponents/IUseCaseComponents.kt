package com.example.myapplication.DI.Components.UseCasesComponents

import com.example.myapplication.DI.Modules.ConnectivityManagerModule
import com.example.myapplication.DI.Modules.UseCaseModules.RepositoryModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.UseCase
import com.example.myapplication.Model.Interactors.MyWeatherForecast.MyWeatherForecastForecastManager
import dagger.Component

@UseCase
@Component(modules = [RepositoryModule::class,ConnectivityManagerModule::class])
interface IUseCaseComponents {
    fun inject(weatherForecastForecastManager: MyWeatherForecastForecastManager)
}