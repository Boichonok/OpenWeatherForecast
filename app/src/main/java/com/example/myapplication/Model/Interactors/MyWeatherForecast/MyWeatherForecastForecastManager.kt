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
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver

import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
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

    //Channel for transmitting event that adding to DB is compleat
    private var publishSubjectAddMyCity = PublishSubject.create<CityCurrentWeatherTable>()

    private lateinit var observerAddingMyCity: DisposableObserver<CityCurrentWeatherTable>

    //Channel for transmitting current forecast
    private var publishSubjectCurrentForecast = PublishSubject.create<CityCurrentWeatherTable>()

    private lateinit var observerCurrentForecast: DisposableObserver<CityCurrentWeatherTable>

    //Chanel for transmitting all my added cities
    private var asyncSubjectAllMyCities = AsyncSubject.create<List<CityCurrentWeatherTable>>()

   // private  lateinit var observeAllMyCities: DisposableObserver<List<CityCurrentWeatherTable>>

    private var isOfflineMode: Boolean = true

    private var isFirstStart: Boolean = true

    constructor() {
        WeatherForecastApplication.getUseCaseComponent().inject(this)
        WeatherForecastApplication.subscribeToUpdateFirstStart(firstStartObserver)
        connectivityManager.registerNetworkCallback(networkBuilder.build(), networkCallback)
    }

    private val firstStartObserver = object : DisposableObserver<Boolean>() {
        override fun onComplete() {

        }

        override fun onNext(t: Boolean?) {
            isFirstStart = t!!
        }

        override fun onError(e: Throwable?) {

        }

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
            if (!isOfflineMode) {
                var lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

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
                        addCityToCurrentWeatherDB(it)
                        publishSubjectCurrentForecast.onNext(it)
                        if (isFirstStart)
                            WeatherForecastApplication.setFinishedFirstStart(context)
                    }, {
                        publishSubjectCurrentForecast.onError(Throwable("updateCurrentCityForecastIsNetworkOnline().ApiRequest: " + it))
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

    private fun addCityToCurrentWeatherDB(city: CityCurrentWeatherTable) {
        disposable.add(
            weatherRoom.getCityWeatherInfoDao().getRowByCityName(city.city_name)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    weatherRoom.getCityWeatherInfoDao().update(it)
                }, {
                    publishSubjectCurrentForecast.onError(Throwable("addCityToCurrentWeatherDB().RoomRequest: " + it.message))
                }, {
                    weatherRoom.getCityWeatherInfoDao().insert(city)
                })
        )
    }

    private fun checkProvider() {
        isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d("UseCase", "E_GPS: " + true)
    }


    override fun subscribeToUpdateCurrentForecastByLocation(observer: DisposableObserver<CityCurrentWeatherTable>) {
        observerCurrentForecast = observer

        if (isFirstStart)
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10f, locationListener)

    }

    override fun stopSearchLocation() {
        locationManager.removeUpdates(locationListener)
    }

    override fun getAllMyCitiesForecasts(observer: DisposableObserver<List<CityCurrentWeatherTable>>) {
        asyncSubjectAllMyCities.subscribe(observer)
        if(!isFirstStart) {
            if(weatherRoom.getCityWeatherInfoDao().getCountRow() > 0) {
                disposable.add(
                    weatherRoom.getCityWeatherInfoDao().getAllRows()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            asyncSubjectAllMyCities.onNext(it)
                            asyncSubjectAllMyCities.onComplete()
                        }, {
                            asyncSubjectAllMyCities.onError(it)
                        })
                )
            }
            else
            {
                asyncSubjectAllMyCities.onError(Throwable("Data base is Empty"))
            }
        }
        else
            asyncSubjectAllMyCities.onError(Throwable("It is first start and DB is Empty"))
    }

    override fun subscribeToUpdateMyCity(observer: DisposableObserver<CityCurrentWeatherTable>) {
        observerAddingMyCity = observer
    }

    override fun addMyCityWithCurrentDayForecast(cityName: String) {
        publishSubjectAddMyCity.subscribe(observerAddingMyCity)
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
                    addCityToCurrentWeatherDB(it)
                    publishSubjectAddMyCity.onComplete()
                },
                    {
                        publishSubjectAddMyCity.onError(Throwable(it.message))
                    })
            )
        } else {
            publishSubjectAddMyCity.onError(Throwable("No Internet connection.\nPleas turn on the Internet!"))
        }
    }

    override fun unsubscribeAll() {
        disposable.clear()
    }

}

/*
*
*
*  disposable.add(
                weatherRoom.getCityWeatherInfoDao().getRowByCityName(cityName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        publishSubjectAddMyCity.onNext(it)
                    }, {
                        publishSubjectAddMyCity.onError(Throwable(it.message))
                    }, {
                        publishSubjectAddMyCity.onError(Throwable("No city with name: " + cityName + " in data base"))
                    })
            )
*
*
* */