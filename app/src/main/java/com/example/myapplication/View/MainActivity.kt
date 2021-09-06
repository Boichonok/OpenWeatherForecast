package com.example.myapplication.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.Model.MY_PERMISSIONS_REQUEST_LOCATION
import com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.Model.loadImage
import com.example.myapplication.Model.requestLocationPermission
import com.example.myapplication.R
import com.example.myapplication.View.Adapters.MyCitiesListAdapter
import com.example.myapplication.View.CustomDialog.EnterMyCityDialog
import com.example.myapplication.View.ViewUtils.SwipeToDeleteCallback
import com.example.myapplication.ViewModel.IWeatherForecastViewModel
import com.example.myapplication.ViewModel.WeatherForecastViewModel
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.getKoin

class MainActivity : AppCompatActivity() {

    private val weatherForecastViewModel: IWeatherForecastViewModel by viewModel(
        null,
        WeatherForecastViewModel::class
    )

    private val myCitiesListAdapter: MyCitiesListAdapter by lazy { MyCitiesListAdapter() }

    private val addMyCityDialog: EnterMyCityDialog.Builder by lazy {
        EnterMyCityDialog.Builder(this)
    }

    private val itemTouchHelper: ItemTouchHelper? by lazy { swipeToDeleteCallback?.let {
        ItemTouchHelper(
            it
        )
    } }

    private var swipeToDeleteCallback: SwipeToDeleteCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestLocationPermission()

        swipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = myCitiesListAdapter
                val id = adapter.removeAndGetSelectedPositionId(viewHolder.adapterPosition)
                weatherForecastViewModel.deleteCityById(id)
            }
        }

        itemTouchHelper?.attachToRecyclerView(my_cities_recycler_view)

        weatherForecastViewModel.currentCityForecast.observe(this) {
            currentCityName.text = getString(R.string.city_name_field, it.city_name)
            country.text = getString(R.string.country_field, it.country)
            temp.text = getString(R.string.temp_field, it.temperature)
            windSpeed.text = getString(R.string.wind_speed, it.wind_speed)
            val iconLink = WeatherClient.ICON_URL_PART1 + it.weather[0].icon + WeatherClient.ICON_URL_PART2
            weather_icon.loadImage(iconLink)
        }

        weatherForecastViewModel.myCitiesList.observe(this, {
            myCitiesListAdapter.setListItems(it)
            my_cities_recycler_view.adapter = myCitiesListAdapter

        })

        add_my_cities.setOnClickListener {
            addMyCityDialog
                .setTitle(getString(R.string.dialog_title))
                .setHintEditText(getString(R.string.city_name_field))
                .setOkButtonClickListener {
                    weatherForecastViewModel.addMyCity(it)
                }.build().showDialog()
        }

        updateWeahterByMyLocation.setOnClickListener {
            weatherForecastViewModel.startSearchLocation()
        }

        weatherForecastViewModel.errors.observe(this, Observer {
            if (it.isNotEmpty())
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        my_cities_recycler_view.layoutManager = LinearLayoutManager(this)

        weatherForecastViewModel.isInternetAvl.observe(this, Observer {
            myCitiesListAdapter.setItemActionActive(it)
        })
        myCitiesListAdapter.setItemClickAction {
            val intent = Intent(this, GoogleMapActivity::class.java)
            intent.putExtra(getString(R.string.intent_city_key), it)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    weatherForecastViewModel.startSearchLocation()
                } else {
                    weatherForecastViewModel.stopSearchLocation()
                }
            }
        }
        return
    }

}
