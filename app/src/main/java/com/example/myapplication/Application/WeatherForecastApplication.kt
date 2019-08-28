package com.example.myapplication.Application

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.example.myapplication.DI.Components.ActivityComponents.DaggerIGoogleMapActivityComponent
import com.example.myapplication.DI.Components.ActivityComponents.IGoogleMapActivityComponent
import com.example.myapplication.DI.Components.DaggerIContextComponent
import com.example.myapplication.DI.Components.IContextComponent
import com.example.myapplication.DI.Components.RepositorySComponents.DaggerIOpenWeatherComponent
import com.example.myapplication.DI.Components.RepositorySComponents.IOpenWeatherComponent
import com.example.myapplication.DI.Components.UseCasesComponents.DaggerIUseCaseComponents
import com.example.myapplication.DI.Components.UseCasesComponents.IUseCaseComponents
import com.example.myapplication.DI.Components.ViewModelComponents.DaggerIViewModelComponent
import com.example.myapplication.DI.Components.ViewModelComponents.IViewModelComponent
import com.example.myapplication.DI.Modules.ActivityModules.GoogleMapActivityModule
import com.example.myapplication.DI.Modules.CompositeDisposableModule
import com.example.myapplication.DI.Modules.ConnectivityManagerModule
import com.example.myapplication.DI.Modules.ContextModule

import com.example.myapplication.DI.Modules.RepositorySModules.OpenWeatherApiModule
import com.example.myapplication.DI.Modules.UseCaseModules.RepositoryModule
import com.example.myapplication.DI.Modules.ViewModelModules.MyWeatherForecastViewModelModule
import io.reactivex.subjects.ReplaySubject


class WeatherForecastApplication : Application() {

    companion object {

        private lateinit var openWeatherComponent: IOpenWeatherComponent
        private lateinit var viewModelComponent: IViewModelComponent
        private lateinit var useCaseComponent: IUseCaseComponents
        private lateinit var googleMapActivityComponent: IGoogleMapActivityComponent
        private lateinit var contextComponent: IContextComponent

        private val APPLICATION_FIRST_STSRT = "WeatherForecastApplication"

        var isFirstStart: Boolean = true
            get() = field
            private set(value) {
                field = value
            }

        var compositeDisposableModule: CompositeDisposableModule = CompositeDisposableModule()

        fun getRepositoryComponent(): IOpenWeatherComponent = openWeatherComponent
        fun getViewModelComponent(): IViewModelComponent = viewModelComponent
        fun getUseCaseComponent(): IUseCaseComponents = useCaseComponent
        fun getGoogleMapActivityComponent(): IGoogleMapActivityComponent = googleMapActivityComponent
        fun getContextComponent(): IContextComponent = contextComponent

        fun setFinishedFirstStart(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(APPLICATION_FIRST_STSRT, false)
                apply()
            }
            isFirstStart = false
        }
    }


    private fun checkFirstStart(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            isFirstStart = getBoolean(APPLICATION_FIRST_STSRT, true)
            Log.d("ViewModel", "IsFirst Start: " + isFirstStart)
        }
    }

    private var openWeatherApiModule: OpenWeatherApiModule = OpenWeatherApiModule()
    private var connectivityManagerModule: ConnectivityManagerModule = ConnectivityManagerModule()
    private var repositoryModule: RepositoryModule = RepositoryModule()
    private var myWeatherForecastViewModelModule: MyWeatherForecastViewModelModule = MyWeatherForecastViewModelModule()
    private var contextModule = ContextModule(this)
    private var googleMapActivityModule = GoogleMapActivityModule()

    override fun onCreate() {
        super.onCreate()
        openWeatherComponent = initRepository()
        viewModelComponent = initViewModel()
        useCaseComponent = initUseCase()
        googleMapActivityComponent = initGoogleMapActivity()
        contextComponent = initContext()

        checkFirstStart(this)
    }

    private fun initContext(): IContextComponent {
        return DaggerIContextComponent.builder().contextModule(contextModule).build()
    }

    private fun initRepository(): IOpenWeatherComponent {
        return DaggerIOpenWeatherComponent
            .builder()
            .openWeatherApiModule(openWeatherApiModule)
            .build()
    }

    private fun initViewModel(): IViewModelComponent {
        return DaggerIViewModelComponent
            .builder()
            .myWeatherForecastViewModelModule(myWeatherForecastViewModelModule)
            .build()
    }

    private fun initUseCase(): IUseCaseComponents {
        return DaggerIUseCaseComponents
            .builder()
            .compositeDisposableModule(compositeDisposableModule)
            .contextModule(contextModule)
            .connectivityManagerModule(connectivityManagerModule)
            .repositoryModule(repositoryModule)
            .build()
    }

    private fun initGoogleMapActivity(): IGoogleMapActivityComponent {
        return DaggerIGoogleMapActivityComponent
            .builder()
            .googleMapActivityModule(googleMapActivityModule)
            .build()
    }

}