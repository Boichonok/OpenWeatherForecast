package com.example.myapplication.Model.Repositories.WeatherRoom.DAO


import io.reactivex.Flowable
import io.reactivex.Maybe

interface IRoomDao<T> {

    fun insert(info: T)

    fun update(info: T)

    fun deleteAll()

    fun deleteByID(id: Int)

    fun getAllTables(): Flowable<List<T>>

    fun getTableByCityName(cityName: String): Maybe<T>

    fun getTableByCityLocation(lat: Double,lon: Double):Flowable<T>

    fun getCountRow(): Int


}