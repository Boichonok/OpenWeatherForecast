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
import com.example.myapplication.R
import com.example.myapplication.ViewModel.IWeatherForecastViewModel
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
            currentCityName.text = it.city_name
            country.text = it.country
            temp.text = it.temperature.toString()
            windSpeed.text = it.wind_speed.toString()
            weather.text = it.weather.toString()
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
