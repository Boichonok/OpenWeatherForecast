package com.example.myapplication.Model.Repository.WeatherRoomSource


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import com.example.myapplication.Model.Repository.WeatherRoomSource.DAO.ICurrentWeatherRoomDao
import com.example.myapplication.Model.Repository.WeatherRoomSource.DAO.IThreeHourForecastDao

@Database(entities = arrayOf(CityCurrentWeatherTable::class,ThreeHourForecastTable::class), version = 1)
//@TypeConverters(ConverterThreeHourForecast::class,ConverterCityCurrentWeather::class)
abstract class IWeatherRoomSource: RoomDatabase() {
    abstract fun getCityWeatherInfoDao(): ICurrentWeatherRoomDao
    abstract fun getThreeHourForecastDao(): IThreeHourForecastDao

}