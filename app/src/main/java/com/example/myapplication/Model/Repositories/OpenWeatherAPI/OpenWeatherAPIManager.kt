package com.example.myapplication.Model.Repositories.OpenWeatherAPI

import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.IWeatherService
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.CurrentWeather.CurrentWeather
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast.ThreeHourForecast
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Constants.Lang
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Constants.Units
import io.reactivex.Flowable
import javax.inject.Inject
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import io.reactivex.Completable
import retrofit2.Call


class OpenWeatherAPIManager: IOpenWeatherAPIManager {


    private val APPID = "appId"
    private val UNITS = "units"
    private val LANGUAGE = "lang"
    private val QUERY = "q"
    private val CITY_ID = "id"
    private val LATITUDE = "lat"
    private val LONGITUDE = "lon"
    private val ZIP_CODE = "zip"


    @Inject
    lateinit var weatherService: IWeatherService

    @Inject
    lateinit var options: LinkedHashMap<String, String>

    constructor()
    {
        WeatherForecastApplication.getRepositoryComponent().injectInOpenWeatherAPIManager(this)

    }

    private fun initOptions(units: String, lang: String)
    {
        options.clear()
        options.put(APPID,WeatherClient.API_KEY)
        options.put(UNITS,units)
        options.put(LANGUAGE,lang)
    }

    override fun getCurrentWeatherByCityName(units: String, lang: String, cityName: String): Flowable<CurrentWeather> {
        initOptions(units,lang)
        options.put(QUERY,cityName)
        return weatherService.getCurrentWeatherByCityName(options)
    }

    override fun getCurrentWeatherByCityId(units: String, lang: String, id: String): Flowable<CurrentWeather> {
        initOptions(units,lang)
        options.put(CITY_ID,id)
        return weatherService.getCurrentWeatherByCityID(options)
    }

    override fun getCurrentWeatherByGeoLocation(units: String, lang: String, coord: Coord): Flowable<CurrentWeather> {
        initOptions(units,lang)
        options.put(LATITUDE,coord.lat.toString())
        options.put(LONGITUDE,coord.lon.toString())

        return weatherService.getCurrentWeatherByGeoCoordinates(options)
    }

    override fun getCurrentWeatherByZipCode(units: String, lang: String, zipCode: String): Flowable<CurrentWeather> {
        initOptions(units,lang)
        options.put(ZIP_CODE,zipCode)
        return weatherService.getCurrentWeatherByZipCode(options)
    }

    override fun getThreeHourForecastByCityName(units: String, lang: String, cityName: String): Flowable<ThreeHourForecast> {
        initOptions(units,lang)
        options.put(QUERY,cityName)
        return weatherService.getThreeHourForecastByCityName(options)
    }

    override fun getThreeHourForecastByCityId(units: String, lang: String, id: String): Flowable<ThreeHourForecast> {
        initOptions(units,lang)
        options.put(CITY_ID,id)
        return weatherService.getThreeHourForecastByCityID(options)
    }

    override fun getThreeHourForecastByGeoLocation(units: String, lang: String, lat: Double, lon: Double): Flowable<ThreeHourForecast> {
        initOptions(units,lang)
        options.put(LATITUDE,lat.toString())
        options.put(LONGITUDE,lon.toString())
        return weatherService.getThreeHourForecastByGeoCoordinates(options)
    }

    override fun getThreeHourForecastByZipCode(units: String, lang: String, zipCode: String): Flowable<ThreeHourForecast> {
        initOptions(units,lang)
        options.put(ZIP_CODE,zipCode)
        return weatherService.getThreeHourForecastByZipCode(options)
    }
}