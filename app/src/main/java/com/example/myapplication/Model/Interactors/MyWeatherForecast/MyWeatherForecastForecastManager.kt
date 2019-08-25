package com.example.myapplication.Model.Interactors.MyWeatherForecast

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.*
import android.os.Bundle
import android.util.Log
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Constants.Lang
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Constants.Units
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.IOpenWeatherAPIManager
import com.example.myapplication.Model.Repositories.WeatherRoom.IWeatherRoom
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastManager
import io.reactivex.*
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

import javax.inject.Inject


class MyWeatherForecastForecastManager : IWeatherForecastManager {

    @Inject
    lateinit var openWeatherAPIManager: IOpenWeatherAPIManager

    @Inject
    lateinit var weatherRoom: IWeatherRoom

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    @Inject
    lateinit var disposable: CompositeDisposable

    @Inject
    lateinit var context: Context

    private var isGPSProviderEnable: Boolean = false

    private var isNetworkProviderEnable: Boolean = false


    private val defaultCityName: String = "Tokyo"

    private val networkBuilder: NetworkRequest.Builder = NetworkRequest.Builder()

    private var publishSubjectCurrentForecast: PublishSubject<CityCurrentWeatherTable> = PublishSubject.create()

    private lateinit var observerCurrentForecast: Observer<CityCurrentWeatherTable>

    private var isOfflineMode: Boolean = true

    constructor() {
        WeatherForecastApplication.getUseCaseComponent().inject(this)
        connectivityManager.registerNetworkCallback(networkBuilder.build(), networkCallback)

    }


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network?) {
            super.onLost(network)
            isOfflineMode = true
            Log.d("ViewModel", "OfflineMode: " + isOfflineMode)
            updateCurrentCityForecastIsNetworkOffline()

        }

        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            isOfflineMode = false
            Log.d("ViewModel", "OfflineMode: " + isOfflineMode)
            if(!isNetworkProviderEnable && !isGPSProviderEnable)
            {
                updateCurrentCityForecastIsNetworkOnline()
            }

        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (!isOfflineMode)
                updateCurrentCitysForecastIsLocationProvidersOnline(Coord(location!!.longitude, location!!.latitude))
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {
            checkProvider()
        }

        override fun onProviderDisabled(provider: String?) {
            checkProvider()
            if(!isOfflineMode)
            updateCurrentCityForecastIsNetworkOnline()
        }

    }

    private fun updateCurrentCitysForecastIsLocationProvidersOnline(currentLocation: Coord) {
        publishSubjectCurrentForecast.subscribe(observerCurrentForecast)
        disposable.add(openWeatherAPIManager.getCurrentWeatherByGeoLocation(
            Units.METRIC,
            Lang.ENGLISH,
            currentLocation
        )
            .map {
                CityCurrentWeatherTable(
                    it.name,
                    it.sys!!.country,
                    it.coord!!,
                    it.main!!.temp.toString(),
                    it.wind!!.speed.toString(),
                    it.weathers!!
                )
            }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                publishSubjectCurrentForecast.onNext(it)
            }, {
                publishSubjectCurrentForecast.onError(Throwable("updateCurrentCitysForecastIsLocationProvidersOnline(): " + it.message))
            })
        )
    }

    private fun updateCurrentCityForecastIsNetworkOnline() {
        checkProvider()
        if (!isNetworkProviderEnable && !isGPSProviderEnable) {
            publishSubjectCurrentForecast.subscribe(observerCurrentForecast)
            disposable.add(
                openWeatherAPIManager.getCurrentWeatherByCityName(Units.METRIC, Lang.ENGLISH, defaultCityName)
                    .map {
                        CityCurrentWeatherTable(
                            it.name,
                            it.sys!!.country,
                            it.coord!!,
                            it.main!!.temp.toString(),
                            it.wind!!.speed.toString(),
                            it.weathers!!
                        )
                    }
                    .distinctUntilChanged()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        disposable.add(
                            weatherRoom.getCityWeatherInfoDao().getTableByCityName(it.city_name)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe({
                                    weatherRoom.getCityWeatherInfoDao().update(it)
                                }, {
                                    publishSubjectCurrentForecast.onError(Throwable("updateCurrentCityForecastIsNetworkOnline().RoomRequest: " + it.message))
                                }, {
                                    weatherRoom.getCityWeatherInfoDao().insert(it)
                                })
                        )
                        publishSubjectCurrentForecast.onNext(it)
                    }, {
                        publishSubjectCurrentForecast.onError(Throwable("updateCurrentCityForecastIsNetworkOnline().ApiRequest: " + it))
                    })
            )
        }
    }

    private fun updateCurrentCityForecastIsNetworkOffline() {
        publishSubjectCurrentForecast.subscribe(observerCurrentForecast)
        disposable.add(
            weatherRoom.getCityWeatherInfoDao().getTableByCityName(defaultCityName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    publishSubjectCurrentForecast.onNext(it)
                }, {
                    publishSubjectCurrentForecast.onError(Throwable("updateCurrentCityForecastIsNetworkOffline(): " + it.message))

                }, {
                    publishSubjectCurrentForecast.onError(Throwable("Current city's forecast DB is empty"))
                })
        )
    }



    override fun subscribeToUpdateCurrentForecastByLocation(observer: Observer<CityCurrentWeatherTable>) {
        observerCurrentForecast = observer

        if (!isOfflineMode) {
            updateCurrentCityForecastIsNetworkOnline()
        }
    }

    private fun checkProvider() {
        isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d("UseCase", "E_GPS: " + true)
    }

    @SuppressLint("MissingPermission")
    override fun startSearchLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10f, locationListener)

    }

    override fun stopSearchLocation() {
        locationManager.removeUpdates(locationListener)
    }


    override fun addMyCityWithCurrentDayForecast(cityName: String): Single<CityCurrentWeatherTable> {
        /* openWeatherAPIManager.clearRequest()
         openWeatherAPIManager.setUnits(Units.METRIC)
         openWeatherAPIManager.setLanguage(Lang.ENGLISH)
         disposable = openWeatherAPIManager.getCurrentWeatherByCityName(cityName)
             .subscribeOn(Schedulers.io())
             .map {
                 CityCurrentWeatherTable(
                     it.name,
                     it.sys!!.country,
                     it.coord!!,
                     it.main!!.temp.toString(),
                     it.wind!!.speed.toString(),
                     it.weathers!!
                 )
             }
             .observeOn(Schedulers.io())
             .subscribe {
                 weatherRoom.getCityWeatherInfoDao().update(it)
             }*/
        return weatherRoom.getCityWeatherInfoDao().getTableByCityName(cityName).toSingle()
    }

    override fun addMyCityWithFiveDayForecast(cityName: String): Single<ThreeHourForecastTable> {
        /*openWeatherAPIManager.clearRequest()
        openWeatherAPIManager.setUnits(Units.METRIC)
        openWeatherAPIManager.setLanguage(Lang.ENGLISH)
        disposable = openWeatherAPIManager.getThreeHourForecastByCityName(cityName)
            .subscribeOn(Schedulers.io())
            .map {
                ThreeHourForecastTable(it.city!!.name, it.city!!.country, it.city!!.coord!!, it.list!!)
            }
            .observeOn(Schedulers.io())
            .subscribe {
                weatherRoom.getThreeHourForecastDao().update(it)
            }*/
        return weatherRoom.getThreeHourForecastDao().getTableByCityName(cityName).toSingle()
    }

    override fun unsubscribeAll() {
        disposable.clear()
    }

}