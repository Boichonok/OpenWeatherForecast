package com.example.myapplication.View

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.DI.Components.ActivityComponents.DaggerIMainActivityComponent
import com.example.myapplication.DI.Modules.ActivityModules.MainActivityModule
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.R
import com.example.myapplication.View.Adapters.MyCitiesListAdapter
import com.example.myapplication.View.CustomDialog.EnterMyCityDialog
import com.example.myapplication.ViewModel.IWeatherForecastViewModel
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*

import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var weatherForecastViewModel: IWeatherForecastViewModel

    @Inject
    lateinit var myCitiesListAdapter: MyCitiesListAdapter

    @Inject
    lateinit var addMyCityDialog: EnterMyCityDialog.Builder

    // var citiesList: ArrayList<CityCurrentWeatherTable> = ArrayList()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainActivityComponent = DaggerIMainActivityComponent.builder()
            .mainActivityModule(MainActivityModule(this))
            .build()
        mainActivityComponent.inject(this)



        Log.d("MainActivity", "OnCreate()")

        weatherForecastViewModel.getWeatherForecastByCurrentLocation().observe(this, Observer {
            currentCityName.text = "" + getString(R.string.city_name_field) + " " + it.city_name
            country.text = "" + getString(R.string.country_field) + " " + it.country
            temp.text = "" + getString(R.string.temp_field) + " " + it.temperature.toString() + getString(R.string.temp_cell)
            windSpeed.text = "" + getString(R.string.wind_speed) + " " + it.wind_speed.toString() + " " + getString(R.string.meter_per_second)
            Picasso.get().load(WeatherClient.ICON_URL_PART1 + "" + it.weather!![0].icon + WeatherClient.ICON_URL_PART2)
                .error(R.drawable.no_connect).into(weather_icon)
        })

        weatherForecastViewModel.getMyCitiesList().observe(this, Observer {
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

        weatherForecastViewModel.getErrors().observe(this, Observer {
            if (it!!.length > 0)
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        my_cities_recycler_view.layoutManager = LinearLayoutManager(this)


        myCitiesListAdapter.setItemClickAction {
                val intent = Intent(this, GoogleMapActivity::class.java)
                intent.putExtra(getString(R.string.intent_city_key), it)
                startActivity(intent) }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume()")
        weatherForecastViewModel.startSearchLocation()


    }

    override fun onPause() {
        super.onPause()
        weatherForecastViewModel.stopSearchLocation()
    }

    override fun onDestroy() {
        super.onDestroy()


    }


}
