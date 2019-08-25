package com.example.myapplication.Application

import android.app.Application
import com.example.myapplication.DI.Components.RepositoryComponents.DaggerIRepositoryComponent
import com.example.myapplication.DI.Components.RepositoryComponents.IRepositoryComponent
import com.example.myapplication.DI.Components.UseCasesComponents.DaggerIUseCaseComponents
import com.example.myapplication.DI.Components.UseCasesComponents.IUseCaseComponents
import com.example.myapplication.DI.Components.ViewModelComponents.DaggerIViewModelComponent
import com.example.myapplication.DI.Components.ViewModelComponents.IViewModelComponent
import com.example.myapplication.DI.Modules.CompositeDisposableModule
import com.example.myapplication.DI.Modules.ConnectivityManagerModule
import com.example.myapplication.DI.Modules.ContextModule

import com.example.myapplication.DI.Modules.RepositoryModules.RepositoryModule
import com.example.myapplication.DI.Modules.UseCaseModules.UseCaseWeatherForecastModule
import com.example.myapplication.DI.Modules.ViewModelModules.MyWeatherForecastViewModelModule


class WeatherForecastApplication : Application() {

    companion object {

        private lateinit var repositoryComponent: IRepositoryComponent
        private lateinit var viewModelComponent: IViewModelComponent
        private lateinit var useCaseComponent: IUseCaseComponents

        var compositeDisposableModule: CompositeDisposableModule = CompositeDisposableModule()

        fun getRepositoryComponent(): IRepositoryComponent = repositoryComponent
        fun getViewModelComponent(): IViewModelComponent = viewModelComponent
        fun getUseCaseComponent(): IUseCaseComponents = useCaseComponent
    }


    private var repositoryModule: RepositoryModule = RepositoryModule()
    private var connectivityManagerModule: ConnectivityManagerModule = ConnectivityManagerModule()
    private var useCaseWeatherForecastModule: UseCaseWeatherForecastModule = UseCaseWeatherForecastModule()
    private var myWeatherForecastViewModelModule: MyWeatherForecastViewModelModule = MyWeatherForecastViewModelModule()
    private var contextModule = ContextModule(this)

    override fun onCreate() {
        super.onCreate()
        repositoryComponent = initRepository()
        viewModelComponent = initViewModel()
        useCaseComponent = initUseCase()
    }

    private fun initRepository(): IRepositoryComponent {
        return DaggerIRepositoryComponent
            .builder()
            //.compositeDisposableModule(compositeDisposableModule)
            .repositoryModule(repositoryModule)
            //.useCaseWeatherForecastModule(useCaseWeatherForecastModule)
            .build()
    }

    private fun initViewModel(): IViewModelComponent {
        return DaggerIViewModelComponent
            .builder()
            .myWeatherForecastViewModelModule(myWeatherForecastViewModelModule)
            //.compositeDisposableModule(compositeDisposableModule)
            .build()
    }

    private fun initUseCase(): IUseCaseComponents
    {
        return DaggerIUseCaseComponents
            .builder()
            .compositeDisposableModule(compositeDisposableModule)
            .contextModule(contextModule)
            .connectivityManagerModule(connectivityManagerModule)
            .useCaseWeatherForecastModule(useCaseWeatherForecastModule)
            .build()
    }



}