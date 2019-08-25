package com.example.myapplication.DI.Modules

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class ConnectivityManagerModule {


    @Provides
    fun getConnectivityManager(context: Context) :ConnectivityManager
    {
        return ContextCompat.getSystemService(context,ConnectivityManager::class.java) as ConnectivityManager
    }

}