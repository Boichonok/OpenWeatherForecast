package com.example.myapplication.Di

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.myapplication.Model.Repository.IOpenWeatherRepository
import com.example.myapplication.Model.Repository.OpenWeatherRepository
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.IOpenWeatherRestApiSource
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.Client.IWeatherClient
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.IWeatherService
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.OpenWeatherRestApiSource
import com.example.myapplication.Model.Repository.WeatherRoomSource.IWeatherRoomSource
import com.example.myapplication.Model.UseCases.MyWeatherForecast.INetworkConnectionUseCase
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IUserLocationUseCase
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastUseCases
import com.example.myapplication.Model.UseCases.MyWeatherForecast.Interactors.NetworkConnectionInteraction
import com.example.myapplication.Model.UseCases.MyWeatherForecast.Interactors.UserLocationInteraction
import com.example.myapplication.Model.UseCases.MyWeatherForecast.Interactors.WeatherForecastInteraction
import com.example.myapplication.ViewModel.WeatherForecastViewModel
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
    single { WeatherForecastInteraction(get(), get()) } bind IWeatherForecastUseCases::class
    single {
        val connectivityManager = provideConnectivityManager(get())
        NetworkConnectionInteraction(connectivityManager)
    } bind INetworkConnectionUseCase::class
    single {
        val fusedLocationClient = provideFusedLocationClient(get())
        UserLocationInteraction(fusedLocationClient)
    } bind IUserLocationUseCase::class
}


private fun provideConnectivityManager(context: Context): ConnectivityManager =
    ContextCompat.getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager

private fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)

val repositoryModule = module {
    single { WeatherClient() } bind IWeatherClient::class
    single {
        val weatherService = getKoin().get<IWeatherClient>().getWeatherAPIClient()
            .create(IWeatherService::class.java)
        OpenWeatherRestApiSource(weatherService)
    } bind IOpenWeatherRestApiSource::class

    single {
        Room.databaseBuilder(
            get(),
            IWeatherRoomSource::class.java,
            "weather_forecast_dataBase"
        ).build()
    } bind IWeatherRoomSource::class

    single { OpenWeatherRepository() } bind IOpenWeatherRepository::class
}

val viewModelModule = module {
    viewModel {
        WeatherForecastViewModel(get(),get(),get())
    }
}



