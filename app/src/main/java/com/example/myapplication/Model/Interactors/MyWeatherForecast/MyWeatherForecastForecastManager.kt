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
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Constants.Lang
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Constants.Units
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.IOpenWeatherAPIManager
import com.example.myapplication.Model.Repositories.WeatherRoom.IWeatherRoom
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastManager
import io.reactivex.Completable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.disposables.CompositeDisposable

import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

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

    //Channel for transmitting event that adding to DB is compleat
    private var publishSubjectAddMyCity = PublishSubject.create<CityCurrentWeatherTable>()

    //Channel for transmitting current forecast
    private var publishSubjectCurrentForecast = PublishSubject.create<CityCurrentWeatherTable>()

    private lateinit var observerCurrentForecast: Observer<CityCurrentWeatherTable>

    //Chanel for transmitting all my added cities
    private var publishSubjectAllMyCities = PublishSubject.create<List<CityCurrentWeatherTable>>()

    private var isOfflineMode: Boolean = false

    constructor() {
        WeatherForecastApplication.getUseCaseComponent().inject(this)
        connectivityManager.registerNetworkCallback(networkBuilder.build(), networkCallback)
    }


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network?) {
            super.onLost(network)

            isOfflineMode = true
            updateCurrentCityForecastIsNetworkOffline()

        }

        override fun onAvailable(network: Network?) {
            super.onAvailable(network)

            isOfflineMode = false
            if (!isNetworkProviderEnable && !isGPSProviderEnable) {
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

        @SuppressLint("MissingPermission")
        override fun onProviderDisabled(provider: String?) {
            checkProvider()
            if (!isOfflineMode && !WeatherForecastApplication.isFirstStart) {
                var lastKnownLocation = locationManager.getLastKnownLocation(provider)
                if (lastKnownLocation != null)
                    updateCurrentCitysForecastIsLocationProvidersOnline(
                        Coord(
                            lastKnownLocation.longitude,
                            lastKnownLocation.latitude
                        )
                    )
            }
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
            .delay(2, TimeUnit.SECONDS)
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

                    .retry()
                    .distinctUntilChanged()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        publishSubjectCurrentForecast.onNext(it)
                        Log.d(
                            "UseCase",
                            "isFirstStart: " + WeatherForecastApplication.isFirstStart + " updateCurrentCityForecastIsNetworkOnline().City: " + it.city_name
                        )
                        disposable.add(addCityToCurrentWeatherDB(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe {
                                if (WeatherForecastApplication.isFirstStart)
                                    WeatherForecastApplication.setFinishedFirstStart(context)
                            })
                    }, {
                        publishSubjectCurrentForecast.onError(Throwable(it.message))
                    })
            )
        }
    }

    private fun updateCurrentCityForecastIsNetworkOffline() {
        publishSubjectCurrentForecast.subscribe(observerCurrentForecast)
        disposable.add(
            weatherRoom.getCityWeatherInfoDao().getRowByCityName(defaultCityName)
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

    private fun addCityToCurrentWeatherDB(city: CityCurrentWeatherTable): Completable {
        return Completable.create {
            var emitter = it!!
            disposable.add(
                weatherRoom.getCityWeatherInfoDao().getRowByCityName(city.city_name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({
                        weatherRoom.getCityWeatherInfoDao().update(it)
                        Log.d("UseCase", "addCityToCurrentWeatherDB().City: " + city.city_name + " Update()")
                        emitter.onComplete()
                    }, {
                        publishSubjectCurrentForecast.onError(Throwable("addCityToCurrentWeatherDB().RoomRequest: " + it.message))
                    }, {
                        weatherRoom.getCityWeatherInfoDao().insert(city)
                        Log.d("UseCase", "addCityToCurrentWeatherDB().City: " + city.city_name + " Insert()")
                        emitter.onComplete()
                    })
            )
        }
    }

    private fun checkProvider() {
        isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    override fun subscribeToUpdateCurrentForecastByLocation(observer: Observer<CityCurrentWeatherTable>) {
        observerCurrentForecast = observer

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED
            || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
        ) {
            isOfflineMode = false
        }
        else
        {
            isOfflineMode = true
        }

        if (WeatherForecastApplication.isFirstStart)
            if (!isOfflineMode) {
                updateCurrentCityForecastIsNetworkOnline()
            } else {
                publishSubjectCurrentForecast.subscribe(observerCurrentForecast)
                publishSubjectCurrentForecast.onError(Throwable("It's first start! That's why your need Internet\nfor init default city's data."))
            }
        else {
            if (!isOfflineMode)
                updateCurrentCityForecastIsNetworkOnline()
            else
                updateCurrentCityForecastIsNetworkOffline()
        }
    }

    @SuppressLint("MissingPermission")
    override fun startSearchLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, locationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, locationListener)
    }

    override fun stopSearchLocation() {
        locationManager.removeUpdates(locationListener)
    }

    override fun getAllMyCitiesForecasts(observer: Observer<List<CityCurrentWeatherTable>>) {
        publishSubjectAllMyCities.subscribe(observer)

        disposable.add(
            weatherRoom.getCityWeatherInfoDao().getAllRows(defaultCityName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("UseCase", "getAllMyCitiesForecasts.Success().list.size: " + it.size)
                    publishSubjectAllMyCities.onNext(it)
                    //publishSubjectAllMyCities.onComplete()
                }, {
                    publishSubjectAllMyCities.onError(it)
                }, {
                    publishSubjectAllMyCities.onError(Throwable("Maybe.OnCompleat(): Db is empty"))
                })
        )

    }



    override fun addMyCityWithCurrentDayForecast(cityName: String,observer: Observer<CityCurrentWeatherTable>) {
        if (!isOfflineMode) {
            disposable.add(openWeatherAPIManager.getCurrentWeatherByCityName(Units.METRIC, Lang.ENGLISH, cityName)
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("UseCase", "addingMyCity..().City: " + it.city_name)
                    disposable.add(addCityToCurrentWeatherDB(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe {
                            Log.d("UseCase","Added Compleat()")
                            publishSubjectAddMyCity.subscribe(observer)
                            publishSubjectAddMyCity.onComplete()
                        })

                },
                    {
                        publishSubjectAddMyCity.subscribe(observer)
                        publishSubjectAddMyCity.onError(Throwable(it.message))
                    }
                )
            )
        } else {
            publishSubjectAddMyCity.onError(Throwable("No Internet connection.\nPleas turn on the Internet!"))
        }
    }

    override fun unsubscribeAll() {
        disposable.clear()
    }

}
