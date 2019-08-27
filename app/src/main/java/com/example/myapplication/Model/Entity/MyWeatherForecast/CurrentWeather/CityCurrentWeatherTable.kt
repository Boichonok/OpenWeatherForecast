package com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather

import androidx.room.*

import androidx.annotation.NonNull
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Coord
import com.example.myapplication.Model.Entity.WeatherApiPojos.POJO.Weather
import com.example.myapplication.Model.Repositories.WeatherRoom.ConverterCityCurrentWeather
import java.util.
*import java.io.Serializable;


@SuppressWarnings("serial")
@Entity(tableName = "city_current_weather_table")
class CityCurrentWeatherTable() : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @NonNull
    @ColumnInfo(name = "city_name")
    var city_name: String = ""

    @NonNull
    @ColumnInfo(name = "country")
    var country: String = ""

    @Embedded
    var coord: Coord = Coord()

    @NonNull
    @ColumnInfo(name = "temperature")
    var temperature: String = ""

    @NonNull
    @ColumnInfo(name = "wind_speed")
    var wind_speed: String = ""

    @NonNull
    //@Embedded(prefix = "weatherInfo")
    @TypeConverters(ConverterCityCurrentWeather::class)
    //@SerializedName("weather")
    var weather: LinkedList<Weather> = LinkedList<Weather>()

    @Ignore
    constructor(
        city_name: String,
        country: String,
        coord: Coord,
        temperature: String,
        wind_speed: String,
        weathers: LinkedList<Weather>
    ) : this() {
        this.city_name = city_name
        this.country = country
        this.coord = coord
        this.temperature = temperature
        this.wind_speed = wind_speed
        this.weather = weathers
    }
}