package com.example.myapplication.Model.Interactors.MyWeatherForecast

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.*
import android.os.Bundle
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

    //Channel for transmitting all errors
    private var publicSubjectError = PublishSubject.create<String>()

    //Changel for transmitting Internet connection state
    private var publishSubjectInternetConnection = PublishSubject.create<Boolean>()


    constructor() {
        WeatherForecastApplication.getUseCaseComponent().inject(this)
        connectivityManager.registerNetworkCallback(networkBuilder.build(), networkCallback)

    }


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network?) {
            super.onLost(network)
            publishSubjectInternetConnection.onNext(false)
            updateCurrentCityForecastIsNetworkOffline()

        }

        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            publishSubjectInternetConnection.onNext(true)
            updateCurrentCityForecastIsNetworkOnline()
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (checkInternetConnected())
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
            if (checkInternetConnected() && !WeatherForecastApplication.isFirstStart) {
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
            .delay(2,TimeUnit.SECONDS)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                publishSubjectCurrentForecast.onNext(it)
            }, {
                publicSubjectError.onNext(it.localizedMessage)
            })
        )
    }


    private fun updateCurrentCityForecastIsNetworkOnline() {
        checkProvider()
        if (!isNetworkProviderEnable && !isGPSProviderEnable) {
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
                        ).apply {
                            this.id = 0
                        }
                    }
                    .retryWhen
                    {
                        it.take(2).delay(5, TimeUnit.SECONDS)
                    }
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        publishSubjectCurrentForecast.onNext(it)

                        disposable.add(addCityToCurrentWeatherDB(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe {
                                if (WeatherForecastApplication.isFirstStart)
                                    WeatherForecastApplication.setFinishedFirstStart(context)
                            })
                    }, {
                        publicSubjectError.onNext(it.localizedMessage)
                    })
            )
        }
    }

    private fun updateCurrentCityForecastIsNetworkOffline() {
        disposable.add(
            weatherRoom.getCityWeatherInfoDao().getRowByCityName(defaultCityName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    publishSubjectCurrentForecast.onNext(it)
                }, {
                    publicSubjectError.onNext(it.localizedMessage)

                }, {
                    publicSubjectError.onNext("Default City does not exist in DB")
                })
        )
    }

    private fun addCityToCurrentWeatherDB(city: CityCurrentWeatherTable): Completable {
        return Completable.create {
            var emitter = it!!
            disposable.add(
                weatherRoom.getCityWeatherInfoDao().getRowByCityNameAndCountry(city.city_name,city.country)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({
                        it.weather = city.weather
                        it.coord = city.coord
                        it.wind_speed = city.wind_speed
                        it.temperature = city.temperature
                        //it.country = city.country
                        //it.city_name = city.city_name
                        weatherRoom.getCityWeatherInfoDao().update(it)

                        emitter.onComplete()
                    }, {
                        publicSubjectError.onNext(it.localizedMessage)
                    }, {
                        weatherRoom.getCityWeatherInfoDao().insert(city)
                        emitter.onComplete()
                    })
            )
        }
    }

    private fun checkProvider() {
        isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkInternetConnected(): Boolean {
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED
            || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
        ) {
            publishSubjectInternetConnection.onNext(true)
            return true
        } else {
            publishSubjectInternetConnection.onNext(false)
            return false
        }
    }

    override fun subscribeToUpdateCurrentForecastByLocation(observer: Observer<CityCurrentWeatherTable>) {
        observerCurrentForecast = observer
        publishSubjectCurrentForecast.subscribe(observer)
        if (WeatherForecastApplication.isFirstStart)
            if (checkInternetConnected()) {
                updateCurrentCityForecastIsNetworkOnline()
            } else {
                publicSubjectError.onNext("It's first start! That's why your need the Internet.")
            }
        else {
            if (checkInternetConnected())
                updateCurrentCityForecastIsNetworkOnline()
            else
                updateCurrentCityForecastIsNetworkOffline()
        }
    }

    @SuppressLint("MissingPermission")
    override fun startSearchLocation() {
        checkInternetConnected()
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
                    publishSubjectAllMyCities.onNext(it)
                }, {
                    publicSubjectError.onNext(it.localizedMessage)
                }, {
                    publicSubjectError.onNext("Can not get data from Db! It is empty!")
                })
        )

    }


    override fun addMyCityWithCurrentDayForecast(cityName: String, observer: Observer<CityCurrentWeatherTable>) {
        if (checkInternetConnected()) {
            disposable.add(openWeatherAPIManager.getCurrentWeatherByCityName(Units.METRIC, Lang.ENGLISH, cityName)
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
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    disposable.add(addCityToCurrentWeatherDB(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe {
                            publishSubjectAddMyCity.subscribe(observer)
                            publishSubjectAddMyCity.onComplete()
                        })

                },
                    {
                        publicSubjectError.onNext(it.localizedMessage)
                    }
                )
            )
        } else {
            publicSubjectError.onNext("No Internet connection.\nPleas turn on the Internet!")
        }
    }

    override fun deleteMyCityByID(id: Int): Boolean {
        var isComplet = false
        disposable.add(Completable.fromAction {
            weatherRoom.getCityWeatherInfoDao().deleteByID(id)
        }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                isComplet = true
            })
        return isComplet
    }

    override fun unsubscribeAll() {
        disposable.clear()
    }

    override fun subscribeToErrorHandler(observer: Observer<String>) {
        publicSubjectError.subscribe(observer)
    }

    override fun subscribeToObserveInternetStateConnection(observer: Observer<Boolean>) {
        publishSubjectInternetConnection.observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }
}
