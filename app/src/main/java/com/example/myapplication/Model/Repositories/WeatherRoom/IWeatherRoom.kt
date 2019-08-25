package com.example.myapplication.Model.Repositories.WeatherRoom


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast.ThreeHourForecastTable
import com.example.myapplication.Model.Repositories.WeatherRoom.DAO.ICurrentWeatherRoomDao
import com.example.myapplication.Model.Repositories.WeatherRoom.DAO.IThreeHourForecastDao

@Database(entities = arrayOf(CityCurrentWeatherTable::class,ThreeHourForecastTable::class), version = 1)
//@TypeConverters(ConverterThreeHourForecast::class,ConverterCityCurrentWeather::class)
abstract class IWeatherRoom: RoomDatabase() {
    abstract fun getCityWeatherInfoDao(): ICurrentWeatherRoomDao
    abstract fun getThreeHourForecastDao(): IThreeHourForecastDao

}