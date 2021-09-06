package com.example.myapplication.Model.UseCases.MyWeatherForecast.Interactors

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import com.example.myapplication.Model.UseCases.MyWeatherForecast.INetworkConnectionUseCase
import com.example.myapplication.Model.asyncScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkConnectionInteraction(
    connectivityManager: ConnectivityManager
) : INetworkConnectionUseCase {


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            updateNetworkState(true)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            updateNetworkState(false)
        }
    }

    private val _networkAvlChanges = MutableStateFlow(false)

    override val networkAvlChanges: StateFlow<Boolean> = _networkAvlChanges
    

    init {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback
        )
    }

    private fun updateNetworkState(isAvl: Boolean) {
        asyncScope.launch {
            _networkAvlChanges.emit(isAvl)
        }
    }
}