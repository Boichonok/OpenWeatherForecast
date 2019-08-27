package com.example.myapplication.Model.Repositories.WeatherRoom.DAO


import androidx.room.*
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable


@Dao
interface ICurrentWeatherRoomDao: IRoomDao<CityCurrentWeatherTable> {
    @Insert
    override fun insert(cityCurrentWeatherTable: CityCurrentWeatherTable)

    @Update
    override fun update(cityCurrentWeatherTable: CityCurrentWeatherTable)

    @Query("DELETE FROM city_current_weather_table")
    override fun deleteAll()

    @Query("DELETE FROM city_current_weather_table WHERE id == :id")
    override fun deleteByID(id: Int)

    @Query("SELECT * FROM city_current_weather_table WHERE city_name != :defaultCityName")
    override fun getAllRows(defaultCityName: String):Maybe<List<CityCurrentWeatherTable>>

    @Query("SELECT * FROM city_current_weather_table WHERE city_Name == :cityName")
    override fun getRowByCityName(cityName: String): Maybe<CityCurrentWeatherTable>

    @Query("SELECT COUNT(city_name) FROM city_current_weather_table")
    override fun getCountRow(): Observable<Int>


}