package com.example.myapplication.DI.Modules.UseCaseModules

import androidx.room.Room
import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.myapplication.DI.Modules.CompositeDisposableModule
import com.example.myapplication.DI.Modules.ContextModule
import com.example.myapplication.DI.ScopeAnnotations.RepositoryScopes.UseCase
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.IOpenWeatherAPIManager
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.OpenWeatherAPIManager
import com.example.myapplication.Model.Repositories.WeatherRoom.IWeatherRoom
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class,CompositeDisposableModule::class])
class RepositoryModule {

    @UseCase
    @Provides
    fun getWeatherRoom(context: Context): IWeatherRoom
    {
        return Room.databaseBuilder(context,
            IWeatherRoom::class.java,
            "weather_forecast_dataBase").build()
    }

    @UseCase
    @Provides
    fun getOpenWeatherAPIManager(): IOpenWeatherAPIManager
    {
        return OpenWeatherAPIManager()
    }

    @UseCase
    @Provides
    fun locationManager(context: Context): LocationManager
    {
        var locationManager =  ContextCompat.getSystemService(context, LocationManager::class.java) as LocationManager
        return locationManager
    }

}