package com.example.myapplication.View

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import android.os.Bundle
import android.view.View
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.R
import com.example.myapplication.View.Adapters.PicassoMarker
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.android.synthetic.main.map_activity.*
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.map_marker_info.view.*
import javax.inject.Inject
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import android.graphics.Canvas
import android.content.res.Resources.NotFoundException
import com.google.android.gms.maps.model.MapStyleOptions
import android.content.res.Resources


class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var markerOptions: MarkerOptions

    @Inject
    lateinit var picassoMarker: PicassoMarker

    private val MAP_VIEW_BUNDLE_KEY = "GoogleMapActivity"

    private lateinit var city: CityCurrentWeatherTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)
        WeatherForecastApplication.getGoogleMapActivityComponent().inject(this)
        loadAndCreateGoogleMapActivityState(savedInstanceState)
        city = intent.getSerializableExtra(getString(R.string.intent_city_key)) as CityCurrentWeatherTable

    }

    private fun loadAndCreateGoogleMapActivityState(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        google_map_view.onCreate(mapViewBundle)
        google_map_view.getMapAsync(this)
    }

    private fun saveGoogleMapActivityState(outState: Bundle) {
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        google_map_view.onSaveInstanceState(mapViewBundle)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveGoogleMapActivityState(outState)
    }

    override fun onResume() {
        super.onResume()
        google_map_view.onResume()
    }

    override fun onStart() {
        super.onStart()
        google_map_view.onStart()
    }

    override fun onStop() {
        super.onStop()
        google_map_view.onStop()
    }

    override fun onPause() {
        google_map_view.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        google_map_view.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        google_map_view.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap!!.setMinZoomPreference(12f)
        initGoogleMapSettings(googleMap)

        setCustomMapStyle(googleMap)

        var markerPosition = setCustomizationForMarker(googleMap,city)


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition))

    }

    private fun setCustomMapStyle(googleMap: GoogleMap?)
    {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.google_map_style
                )
            )

            if (!success) {

            }
        } catch (e: Resources.NotFoundException) {

        }

    }


    private fun setCustomizationForMarker(googleMap: GoogleMap?,city: CityCurrentWeatherTable): LatLng
    {
        val cityPosition = LatLng(city.coord.lat, city.coord.lon)
        val weatherIcon = WeatherClient.ICON_URL_PART1 + "" + city.weather!![0].icon + WeatherClient.ICON_URL_PART2
        val cityName = getString(R.string.city_name_field) + " " + city.city_name
        val cityTemp = getString(R.string.temp_field) + " " + city.temperature + getString(R.string.temp_cell)

        markerOptions.position(cityPosition)

        val marker = googleMap!!.addMarker(markerOptions)

        picassoMarker.setMarker(marker)
        Picasso.get()
            .load(weatherIcon)
            .into(picassoMarker)

        googleMap!!.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter
        {
            override fun getInfoWindow(marker: Marker?): View? {
                return null
            }

            override fun getInfoContents(maarker: Marker?): View {
                var view = layoutInflater.inflate(R.layout.map_marker_info,null)

                view.marker_city.text = cityName
                view.marker_temp.text = cityTemp
                Picasso
                    .get()
                    .load(weatherIcon)
                    .into(view.marker_icon_weather)


                return view
            }
        })
        return cityPosition
    }

    private fun initGoogleMapSettings(googleMap: GoogleMap?) {
        val uiSettings = googleMap!!.getUiSettings()
        uiSettings.setIndoorLevelPickerEnabled(true)
        uiSettings.setMapToolbarEnabled(true)
        uiSettings.setCompassEnabled(true)
        uiSettings.setZoomControlsEnabled(true)

    }
}