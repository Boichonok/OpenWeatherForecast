package com.example.myapplication.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.DI.Components.ActivityComponents.DaggerIMainActivityComponent
import com.example.myapplication.DI.Modules.ActivityModules.MainActivityModule
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.R
import com.example.myapplication.ViewModel.IWeatherForecastViewModel
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var weatherForecastViewModel: IWeatherForecastViewModel


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainActivityComponent = DaggerIMainActivityComponent.builder()
            //.compositeDisposableModule(WeatherForecastApplication.compositeDisposableModule)
            .mainActivityModule(MainActivityModule(this))
            .build()
        mainActivityComponent.inject(this)
        Log.d("MainActivity", "OnCreate()")

        weatherForecastViewModel.getWeatherForecastByCurrentLocation().observe(this, Observer {
            currentCityName.text = "" + R.string.city_name_field +  it.city_name
            country.text = "" + R.string.country_field + it.country
            temp.text = "" + R.string.temp_field + it.temperature.toString()
            windSpeed.text = "" + R.string.wind_speed + it.wind_speed.toString()
            Picasso.get().load(WeatherClient.ICON_URL_PART1 + it.weather!![0].icon + WeatherClient.ICON_URL_PART2).error(R.drawable.no_connect).into(weather_icon)
        })

    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume()")
        weatherForecastViewModel.startSearchLocation()


    }

    override fun onPause() {
        super.onPause()
        weatherForecastViewModel.stopSearchLocation()
    }

    override fun onDestroy() {
        super.onDestroy()


    }


}
