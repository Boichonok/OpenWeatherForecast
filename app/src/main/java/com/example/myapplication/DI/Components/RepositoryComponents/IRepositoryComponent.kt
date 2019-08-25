package com.example.myapplication.DI.Components.RepositoryComponents

import com.example.myapplication.DI.Modules.RepositoryModules.RepositoryModule
import com.example.myapplication.DI.Modules.UseCaseModules.UseCaseWeatherForecastModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.Repository
import com.example.myapplication.Model.Interactors.MyWeatherForecast.MyWeatherForecastForecastManager
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.OpenWeatherAPIManager
import dagger.Component

@Repository
@Component(modules = [RepositoryModule::class])
interface IRepositoryComponent {
    fun injectInOpenWeatherAPIManager(openWeatherAPIManager: OpenWeatherAPIManager)
}