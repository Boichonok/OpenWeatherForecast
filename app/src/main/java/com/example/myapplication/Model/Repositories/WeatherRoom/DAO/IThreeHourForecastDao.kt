package com.example.myapplication.Model.Repositories.WeatherRoom.DAO

import androidx.room.*
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface IThreeHourForecastDao: IRoomDao<ThreeHourForecastTable> {
    @Insert
    override fun insert(threeHourForecastTable: ThreeHourForecastTable)

    @Update
    override fun update(threeHourForecastTable: ThreeHourForecastTable)

    @Query("DELETE FROM three_hour_forecast_table")
    override fun deleteAll()

    @Query("DELETE FROM three_hour_forecast_table WHERE id == :id")
    override fun deleteByID(id: Int)

    @Query("SELECT * FROM three_hour_forecast_table WHERE id == :id")
    override fun getRowByID(id: Int): Maybe<ThreeHourForecastTable>

    @Query("SELECT * FROM three_hour_forecast_table WHERE city_name != :defaultCityName")
    override fun getAllRows(defaultCityName: String): Maybe<List<ThreeHourForecastTable>>

    @Query("SELECT * FROM three_hour_forecast_table WHERE city_name == :cityName")
    override fun getRowByCityName(cityName: String): Maybe<ThreeHourForecastTable>

    @Query("SELECT COUNT(city_name) FROM three_hour_forecast_table")
    override fun getCountRow(): Observable<Int>


}