package com.example.myapplication.Application

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.example.myapplication.Di.repositoryModule
import com.example.myapplication.Di.useCaseModule
import com.example.myapplication.Di.viewModelModule
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Constants.Lang
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class WeatherForecastApplication : Application() {

    companion object {

       private const val APPLICATION_FIRST_START = "WeatherForecastApplication"

        var isFirstStart: Boolean = true
            private set

        const val appLang = Lang.ENGLISH

        fun setFinishedFirstStart(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(APPLICATION_FIRST_START, false)
                apply()
            }
            isFirstStart = false
        }
    }


    private fun checkFirstStart(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            isFirstStart = getBoolean(APPLICATION_FIRST_START, true)
            Log.d("ViewModel", "IsFirst Start: $isFirstStart")
        }
    }


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherForecastApplication)
            modules(
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }

        checkFirstStart(this)
    }
}