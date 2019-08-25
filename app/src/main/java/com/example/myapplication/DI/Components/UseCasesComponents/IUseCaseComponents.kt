package com.example.myapplication.DI.Components.UseCasesComponents

import com.example.myapplication.DI.Modules.ConnectivityManagerModule
import com.example.myapplication.DI.Modules.UseCaseModules.UseCaseWeatherForecastModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.UseCase
import com.example.myapplication.Model.Interactors.MyWeatherForecast.MyWeatherForecastForecastManager
import dagger.Component
import dagger.Module

@UseCase
@Component(modules = [UseCaseWeatherForecastModule::class,ConnectivityManagerModule::class])
interface IUseCaseComponents {
    fun inject(weatherForecastForecastManager: MyWeatherForecastForecastManager)
}