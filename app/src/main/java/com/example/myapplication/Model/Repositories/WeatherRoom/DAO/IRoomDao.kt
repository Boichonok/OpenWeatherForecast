package com.example.myapplication.Model.Repositories.WeatherRoom.DAO


import io.reactivex.Maybe
import io.reactivex.Observable

interface IRoomDao<T> {

    fun insert(info: T)

    fun update(info: T)

    fun deleteAll()

    fun deleteByID(id: Int)

    fun getRowByID(id: Int): Maybe<T>

    fun getAllRows(defaultCityName: String): Maybe<List<T>>

    fun getRowByCityName(cityName: String): Maybe<T>

    fun getRowByCityNameAndCountry(cityName: String,country : String): Maybe<T>

    fun getCountRow(): Observable<Int>


}