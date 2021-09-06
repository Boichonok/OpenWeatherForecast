package com.example.myapplication.Model

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.R
import com.example.myapplication.View.MainActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

val asyncScope: CoroutineScope by lazy {
    CoroutineScope(Dispatchers.IO + CoroutineName("asyncScope"))
}

const val MY_PERMISSIONS_REQUEST_LOCATION = 99

fun AppCompatActivity.requestLocationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }
}

val Context.isGeolocationEnabled: Boolean
    get() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return manager != null && (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

fun ImageView.loadImage(url: String) {
    val picassoRequestCreator = Picasso.get()
            .load(url)
            .error(R.drawable.no_connect)

    val startLoadTime = System.currentTimeMillis()
    picassoRequestCreator.into(this,object : Callback {
        override fun onSuccess() {
            Log.d(
                Picasso::class.simpleName,"onSuccess::End Load Img TIme: ${
                System.currentTimeMillis() - startLoadTime
            } ms")
        }

        override fun onError(e: Exception?) {
            Log.d(
                Picasso::class.simpleName,"onError::End Load Img TIme: ${
                    System.currentTimeMillis() - startLoadTime
                } ms\nError: $e")
        }
    })
}