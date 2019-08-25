package com.example.myapplication.DI.Components.ViewModelComponents

import com.example.myapplication.DI.Modules.ViewModelModules.MyWeatherForecastViewModelModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.UseCase
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.ViewModel
import com.example.myapplication.ViewModel.WeatherForecastViewModel
import dagger.Component

@ViewModel
@Component(modules = [MyWeatherForecastViewModelModule::class])
interface IViewModelComponent {
    fun inject(weatherForecastViewModel: WeatherForecastViewModel)

}