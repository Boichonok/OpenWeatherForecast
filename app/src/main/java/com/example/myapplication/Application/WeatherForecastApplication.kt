package com.example.myapplication.Application

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
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
import io.reactivex.Observer
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject


class WeatherForecastApplication : Application() {

    companion object {

        private lateinit var repositoryComponent: IRepositoryComponent
        private lateinit var viewModelComponent: IViewModelComponent
        private lateinit var useCaseComponent: IUseCaseComponents

        private val APPLICATION_FIRST_STSRT = "WeatherForecastApplication"
        private val publishSubjectFirstStart = PublishSubject.create<Boolean>()
        private lateinit var firstStartObserver: DisposableObserver<Boolean>

        var compositeDisposableModule: CompositeDisposableModule = CompositeDisposableModule()

        fun getRepositoryComponent(): IRepositoryComponent = repositoryComponent
        fun getViewModelComponent(): IViewModelComponent = viewModelComponent
        fun getUseCaseComponent(): IUseCaseComponents = useCaseComponent

        fun subscribeToUpdateFirstStart(observer: DisposableObserver<Boolean>)
        {
            firstStartObserver = observer
        }
        fun setFinishedFirstStart(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(APPLICATION_FIRST_STSRT, true)
                apply()
                publishSubjectFirstStart.subscribe(firstStartObserver)
                publishSubjectFirstStart.onNext(true)
            }
        }
    }


    private fun checkFirstStart(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            var isFirstStart = getBoolean(APPLICATION_FIRST_STSRT, false)
            publishSubjectFirstStart.subscribe(firstStartObserver)
            publishSubjectFirstStart.onNext(isFirstStart)
        }
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

        checkFirstStart(this)
    }

    private fun initRepository(): IRepositoryComponent {
        return DaggerIRepositoryComponent
            .builder()
            .repositoryModule(repositoryModule)
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
            .useCaseWeatherForecastModule(useCaseWeatherForecastModule)
            .build()
    }


}