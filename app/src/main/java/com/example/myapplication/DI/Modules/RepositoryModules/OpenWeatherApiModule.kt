package com.example.myapplication.DI.Modules.RepositoryModules

import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.Repository
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.IWeatherService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class OpenWeatherApiModule {

    @Repository
    @Provides
    fun options(): LinkedHashMap<String,String>
    {
        return LinkedHashMap< String,String>()
    }

    @Provides
    fun openWeatherAPIService(retrofit: Retrofit): IWeatherService
    {
        return retrofit.create(IWeatherService::class.java)
    }

    @Repository
    @Provides
    fun openWeatherAPIClient(): Retrofit
    {
        return WeatherClient().getWeatherAPIClient()
    }
}