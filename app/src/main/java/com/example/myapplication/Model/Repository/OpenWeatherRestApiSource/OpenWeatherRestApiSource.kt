package com.example.myapplication.Model.Repository.OpenWeatherRestApiSource

import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.IWeatherService
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.restApiRequest
import java.util.*
import kotlin.collections.LinkedHashMap


class OpenWeatherRestApiSource(private val weatherService: IWeatherService) : IOpenWeatherRestApiSource {


    private val APPID = "appId"
    private val UNITS = "units"
    private val LANGUAGE = "lang"
    private val QUERY = "q"
    private val CITY_ID = "id"
    private val LATITUDE = "lat"
    private val LONGITUDE = "lon"
    private val ZIP_CODE = "zip"

    private fun initOptions(units: String, lang: String): LinkedHashMap<String, String> {
        val options: LinkedHashMap<String, String> = LinkedHashMap()
        options[APPID] = WeatherClient.API_KEY
        options[UNITS] = units
        options[LANGUAGE] = lang
        return options
    }

    override suspend fun getCurrentWeatherByCityName(
        units: String,
        lang: String,
        cityName: String
    ): CityCurrentWeatherTable {
        val options = initOptions(units, lang)
        options[QUERY] = cityName
        return restApiRequest(
            network = {
                weatherService.getCurrentWeatherByCityName(options)
            },
            mapper = {
                CityCurrentWeatherTable(
                    it.name,
                    it.sys?.country ?: "",
                    it.coord ?: Coord(),
                    it.main?.temp.toString(),
                    it.wind?.speed.toString(),
                    it.weathers ?: LinkedList()
                )
            }
        )
    }

    override suspend fun getCurrentWeatherByCityId(
        units: String,
        lang: String,
        id: String
    ): CityCurrentWeatherTable {
        val options = initOptions(units, lang)
        options[CITY_ID] = id
        return restApiRequest(
            network = {
                weatherService.getCurrentWeatherByCityID(options)
            },
            mapper = {
                CityCurrentWeatherTable(
                    it.name,
                    it.sys?.country ?: "",
                    it.coord ?: Coord(),
                    it.main?.temp.toString(),
                    it.wind?.speed.toString(),
                    it.weathers ?: LinkedList()
                )
            }
        )
    }

    override suspend fun getCurrentWeatherByGeoLocation(
        units: String,
        lang: String,
        coord: Coord
    ): CityCurrentWeatherTable {
        val options = initOptions(units, lang)
        options[LATITUDE] = coord.lat.toString()
        options[LONGITUDE] = coord.lon.toString()

        return restApiRequest(
            network = {
                weatherService.getCurrentWeatherByGeoCoordinates(options)
            },
            mapper = {
                CityCurrentWeatherTable(
                    it.name,
                    it.sys?.country ?: "",
                    it.coord ?: Coord(),
                    it.main?.temp.toString(),
                    it.wind?.speed.toString(),
                    it.weathers ?: LinkedList()
                )
            }
        )
    }

    override suspend fun getCurrentWeatherByZipCode(
        units: String,
        lang: String,
        zipCode: String
    ): CityCurrentWeatherTable {
        val options = initOptions(units, lang)
        options[ZIP_CODE] = zipCode
        return restApiRequest(
            network = {
                weatherService.getCurrentWeatherByZipCode(options)
            },
            mapper = {
                CityCurrentWeatherTable(
                    it.name,
                    it.sys?.country ?: "",
                    it.coord ?: Coord(),
                    it.main?.temp.toString(),
                    it.wind?.speed.toString(),
                    it.weathers ?: LinkedList()
                )
            }
        )
    }

    override suspend fun getThreeHourForecastByCityName(
        units: String,
        lang: String,
        cityName: String
    ): ThreeHourForecastTable {
        val options = initOptions(units, lang)
        options[QUERY] = cityName
        return restApiRequest(
            network = {
                weatherService.getThreeHourForecastByCityName(options)
            },
            mapper = {
                ThreeHourForecastTable(
                    it.city?.name ?: cityName,
                    it.city?.country ?: "",
                    it.city?.coord ?: Coord(),
                    it.list ?: LinkedList()
                )
            }
        )
    }

    override suspend fun getThreeHourForecastByCityId(
        units: String,
        lang: String,
        id: String
    ): ThreeHourForecastTable {
        val options = initOptions(units, lang)
        options[CITY_ID] = id
        return restApiRequest(
            network = {
                weatherService.getThreeHourForecastByCityID(options)
            },
            mapper = {
                ThreeHourForecastTable(
                    it.city?.name ?: "",
                    it.city?.country ?: "",
                    it.city?.coord ?: Coord(),
                    it.list ?: LinkedList()
                ).apply { this.id = id.toInt() }
            }
        )
    }

    override suspend fun getThreeHourForecastByGeoLocation(
        units: String,
        lang: String,
        lat: Double,
        lon: Double
    ): ThreeHourForecastTable {
        val options = initOptions(units, lang)
        options[LATITUDE] = lat.toString()
        options[LONGITUDE] = lon.toString()
        return restApiRequest(
            network = {
                weatherService.getThreeHourForecastByGeoCoordinates(options)
            },
            mapper = {
                ThreeHourForecastTable(
                    it.city?.name ?: "",
                    it.city?.country ?: "",
                    it.city?.coord ?: Coord(),
                    it.list ?: LinkedList()
                )
            }
        )
    }

    override suspend fun getThreeHourForecastByZipCode(
        units: String,
        lang: String,
        zipCode: String
    ): ThreeHourForecastTable {
        val options = initOptions(units, lang)
        options[ZIP_CODE] = zipCode
        return restApiRequest(
            network = {
                weatherService.getThreeHourForecastByZipCode(options)
            },
            mapper = {
                ThreeHourForecastTable(
                    it.city?.name ?: "",
                    it.city?.country ?: "",
                    it.city?.coord ?: Coord(),
                    it.list ?: LinkedList()
                )
            }
        )
    }
}