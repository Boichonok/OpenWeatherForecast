package com.example.myapplication.DI.Modules.ActivityModules


import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.DI.Modules.CompositeDisposableModule
import com.example.myapplication.DI.ScopeAnnotations.MainActivityScope
import com.example.myapplication.View.MainActivity
import com.example.myapplication.ViewModel.IWeatherForecastViewModel
import com.example.myapplication.ViewModel.WeatherForecastViewModel
import dagger.Module
import dagger.Provides

@Module//(includes = [CompositeDisposableModule::class])
class MainActivityModule {
    private val mainActivity: MainActivity



    constructor(mainActivity: MainActivity){
        this.mainActivity = mainActivity
    }


    @Provides
    @MainActivityScope
    fun getWeatherForecastViewModel(): IWeatherForecastViewModel
    {
        return ViewModelProviders.of(mainActivity).get(WeatherForecastViewModel::class.java)
    }


}