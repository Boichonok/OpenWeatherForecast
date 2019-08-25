package com.example.myapplication.DI.Modules.ViewModelModules

import com.example.myapplication.DI.Modules.CompositeDisposableModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.ViewModel
import com.example.myapplication.Model.Interactors.MyWeatherForecast.MyWeatherForecastForecastManager
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastManager
import dagger.Module
import dagger.Provides

@Module//(includes = [CompositeDisposableModule::class])
class MyWeatherForecastViewModelModule {




    @ViewModel
    @Provides
    fun getMyWeatherForecastUseCase(): IWeatherForecastManager
    {
        return MyWeatherForecastForecastManager()
    }


}