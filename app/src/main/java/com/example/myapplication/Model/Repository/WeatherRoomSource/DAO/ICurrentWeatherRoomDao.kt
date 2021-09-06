package com.example.myapplication.Model.Repository.WeatherRoomSource.DAO


import androidx.room.*
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import kotlinx.coroutines.flow.Flow


@Dao
interface ICurrentWeatherRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(infoWeatherOfCity: CityCurrentWeatherTable)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(infoWeatherOfCity: CityCurrentWeatherTable)

    @Query("DELETE FROM city_current_weather_table")
    suspend fun deleteAll()

    @Query("DELETE FROM city_current_weather_table WHERE id == :id")
    suspend fun deleteByID(id: Int)

    @Query("SELECT * FROM city_current_weather_table  WHERE id == :id")
    suspend fun getRowByID(id: Int): CityCurrentWeatherTable?

    @Query("SELECT * FROM city_current_weather_table WHERE city_name != :defaultCityName")
    fun getAllRows(defaultCityName: String): Flow<List<CityCurrentWeatherTable>>

    @Query("SELECT * FROM city_current_weather_table WHERE city_Name == :cityName")
    suspend fun getRowByCityName(cityName: String): CityCurrentWeatherTable?

    @Query("SELECT * FROM city_current_weather_table WHERE city_Name == :cityName AND country == :country")
    suspend fun getRowByCityNameAndCountry(cityName: String, country: String): CityCurrentWeatherTable?

    @Query("SELECT COUNT(city_name) FROM city_current_weather_table")
    suspend fun getCountRow(): Int

}