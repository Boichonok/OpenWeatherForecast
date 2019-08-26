package com.example.myapplication.DI.Components.RepositorySComponents

import com.example.myapplication.DI.Modules.RepositorySModules.OpenWeatherApiModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.Repository
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.OpenWeatherAPIManager
import dagger.Component

@Repository
@Component(modules = [OpenWeatherApiModule::class])
interface IOpenWeatherComponent {
    fun injectInOpenWeatherAPIManager(openWeatherAPIManager: OpenWeatherAPIManager)
}