package com.example.myapplication.Model.Repository.WeatherRoomSource.DAO

import androidx.room.*
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import kotlinx.coroutines.flow.Flow

@Dao
interface IThreeHourForecastDao {
    @Insert
    suspend fun insert(info: ThreeHourForecastTable)

    @Update
    suspend fun update(info: ThreeHourForecastTable)

    @Query("DELETE FROM three_hour_forecast_table")
    suspend fun deleteAll()

    @Query("DELETE FROM three_hour_forecast_table WHERE id == :id")
    suspend fun deleteByID(id: Int)

    @Query("SELECT * FROM three_hour_forecast_table WHERE id == :id")
    suspend fun getRowByID(id: Int): ThreeHourForecastTable

    @Query("SELECT * FROM three_hour_forecast_table WHERE city_name != :defaultCityName")
    fun getAllRows(defaultCityName: String): Flow<List<ThreeHourForecastTable>>

    @Query("SELECT * FROM three_hour_forecast_table WHERE city_name == :cityName")
    suspend fun getRowByCityName(cityName: String): ThreeHourForecastTable

    @Query("SELECT * FROM three_hour_forecast_table WHERE city_name == :cityName & country == :country")
    suspend fun getRowByCityNameAndCountry(cityName: String, country: String): ThreeHourForecastTable

    @Query("SELECT COUNT(city_name) FROM three_hour_forecast_table")
    suspend fun getCountRow(): Int


}