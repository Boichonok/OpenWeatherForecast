package com.example.myapplication.Model.Entity.MyWeatherForecast.ThreeHourForecast

import androidx.room.*
import androidx.annotation.NonNull
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Threehourforecast.ThreeHourWeather
import com.example.myapplication.Model.Repository.WeatherRoomSource.ConverterThreeHourForecast
import java.util.*


@Entity(tableName = "three_hour_forecast_table")
class ThreeHourForecastTable() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @NonNull
    @ColumnInfo(name = "city_name")
    var city_name: String = ""

    @NonNull
    @ColumnInfo(name = "country")
    var country: String = ""

    @Embedded
    var coord:Coord = Coord()
    /*@NonNull
    @ColumnInfo(name = "latitude")
    var lat: String = ""

    @NonNull
    @ColumnInfo(name = "longitude")
    var lon: String = ""*/

    //@Embedded
    @TypeConverters(ConverterThreeHourForecast::class)
    var list: LinkedList<ThreeHourWeather> = LinkedList<ThreeHourWeather>()

    @Ignore
    constructor(
        city_name: String,
        country: String,
        coord: Coord,
        weather_info_list: LinkedList<ThreeHourWeather>
    ) : this() {
        this.city_name = city_name
        this.country = country
        this.coord = coord
        this.list = weather_info_list
    }

}