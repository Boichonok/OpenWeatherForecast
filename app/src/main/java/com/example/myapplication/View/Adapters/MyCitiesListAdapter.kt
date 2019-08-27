package com.example.myapplication.View.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_cities_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class MyCitiesListAdapter() : RecyclerView.Adapter<MyCitiesListAdapter.MyCitiesViewHolder>() {



    private var items: List<CityCurrentWeatherTable> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCitiesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_my_cities_list,
            parent, false
        )
        return MyCitiesViewHolder(view = view)
    }

    fun setListItems(items: List<CityCurrentWeatherTable>)
    {
        this.items = items
        Log.d("UseCae","List size: " + this.items.size)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun onBindViewHolder(holder: MyCitiesViewHolder, position: Int) {
        val item = items[position]
        holder.city_name.text = ""  + item.city_name
        holder.country.text = "" + item.country
        holder.temp.text = "" + item.temperature
        Picasso
            .get()
            .load(WeatherClient.ICON_URL_PART1 + item.weather!![0].icon + WeatherClient.ICON_URL_PART2)
            .error(R.drawable.no_connect)
            .into(holder.icon)
        holder.itemView.setOnClickListener {

        }
    }


    class MyCitiesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val city_name = view.item_city_name
        val country = view.item_country
        val temp = view.item_temp
        val icon = view.item_weather_icon
    }
}