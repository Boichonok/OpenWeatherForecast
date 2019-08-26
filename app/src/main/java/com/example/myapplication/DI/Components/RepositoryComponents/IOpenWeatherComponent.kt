package com.example.myapplication.DI.Components.RepositoryComponents

import com.example.myapplication.DI.Modules.RepositoryModules.OpenWeatherApiModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.Repository
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.OpenWeatherAPIManager
import dagger.Component

@Repository
@Component(modules = [OpenWeatherApiModule::class])
interface IOpenWeatherComponent {
    fun injectInOpenWeatherAPIManager(openWeatherAPIManager: OpenWeatherAPIManager)
}