package com.example.myapplication.Model.UseCases.MyWeatherForecast.Interactors

import android.content.Context
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Repository.IOpenWeatherRepository
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Constants.Units
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastUseCases
import com.example.myapplication.Model.UseCases.MyWeatherForecast.IWeatherForecastUseCases.CityLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*


class WeatherForecastInteraction(
    private val openWeatherRepository: IOpenWeatherRepository,
    private val context: Context
) : IWeatherForecastUseCases {

    private val defaultCityName: String = "Tokyo"

    private var cityLocation = MutableStateFlow<CityLocation?>(null)

    private val errorCollector = MutableSharedFlow<Throwable>()

    override val errors: Flow<Throwable> = errorCollector

    override var anyLocationProviderEnabled: Boolean = false

    override val currentCityWeatherForecast: Flow<CityCurrentWeatherTable> = cityLocation.map {
        val weatherForecast: CityCurrentWeatherTable =
            if (WeatherForecastApplication.isFirstStart) {
                getCurrCityWeatherForecast()
            } else {
                getCityWeatherForecastByLastLocation()
            }
        weatherForecast
    }

    override suspend fun saveMyCityWeatherForecast(cityName: String) {
        val actualWeatherForecastInCity =
            openWeatherRepository.restApiSource.getCurrentWeatherByCityName(
                Units.METRIC,
                WeatherForecastApplication.appLang,
                cityName
            )
        openWeatherRepository.roomSource.getCityWeatherInfoDao().insert(actualWeatherForecastInCity)
    }

    override suspend fun deleteMyCityWeatherForecastById(cityId: Int) {
        openWeatherRepository.roomSource.getCityWeatherInfoDao().deleteByID(cityId)
    }

    override fun getAllMySavedCitiesForecast(): Flow<List<CityCurrentWeatherTable>> =
        openWeatherRepository.roomSource.getCityWeatherInfoDao().getAllRows(defaultCityName)

    override suspend fun setCurrCityLocation(cityLocation: CityLocation) {
        this.cityLocation.emit(cityLocation)
        completeFirstStart()
    }

    private suspend fun getCurrCityWeatherForecast(): CityCurrentWeatherTable {
        return if (anyLocationProviderEnabled) {
            getCityWeatherForecastByCurrLocation()
        } else {
            getCityWeatherForecastByLastLocation()
        }
    }

    private suspend fun getCityWeatherForecastByCurrLocation(): CityCurrentWeatherTable {
        cityLocation.first()?.let { currLocation ->
            return openWeatherRepository.restApiSource.getCurrentWeatherByGeoLocation(
                Units.METRIC, WeatherForecastApplication.appLang,
                Coord(
                    lon = currLocation.location.longitude,
                    lat = currLocation.location.latitude
                )
            )
        } ?: run {
            return openWeatherRepository.restApiSource.getCurrentWeatherByCityName(
                Units.METRIC,
                WeatherForecastApplication.appLang, defaultCityName
            )
        }
    }

    private suspend fun getCityWeatherForecastByLastLocation(): CityCurrentWeatherTable {
        val lastCityLocation = cityLocation.first()
        if (lastCityLocation != null) {
            if (lastCityLocation.cityName != null) {
                return openWeatherRepository.roomSource.getCityWeatherInfoDao()
                    .getRowByCityName(lastCityLocation.cityName)?:
                    return openWeatherRepository.restApiSource.getCurrentWeatherByCityName(
                        Units.METRIC,
                        WeatherForecastApplication.appLang,
                        lastCityLocation.cityName
                    )
            } else {
                val lng = lastCityLocation.location.longitude - 0.001
                val lat = lastCityLocation.location.latitude - 0.001
                return openWeatherRepository.restApiSource.getCurrentWeatherByGeoLocation(
                    Units.METRIC,
                    WeatherForecastApplication.appLang,
                    Coord(lon = lng,lat = lat)
                )
            }
        } else {
            return openWeatherRepository.restApiSource.getCurrentWeatherByCityName(
                Units.METRIC,
                WeatherForecastApplication.appLang, defaultCityName
            )
        }
    }

    private fun completeFirstStart() {
        if (WeatherForecastApplication.isFirstStart) {
            WeatherForecastApplication.setFinishedFirstStart(context)
        }
    }
}
