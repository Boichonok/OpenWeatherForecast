package com.example.myapplication.Model.Repositories.WeatherRoom.DAO


import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable

interface IRoomDao<T> {

    fun insert(info: T)

    fun update(info: T)

    fun deleteAll()

    fun deleteByID(id: Int)

    fun getAllRows(): Flowable<List<T>>

    fun getRowByCityName(cityName: String): Maybe<T>

    fun getCountRow(): Observable<Int>


}