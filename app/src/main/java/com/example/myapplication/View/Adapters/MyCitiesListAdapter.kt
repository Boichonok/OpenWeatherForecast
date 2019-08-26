package com.example.myapplication.View.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Entity.MyWeatherForecast.CurrentWeather.CityCurrentWeatherTable
import com.example.myapplication.Model.Repositories.OpenWeatherAPI.Network.WeatherAPI.Client.WeatherClient
import com.example.myapplication.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_my_cities_list.view.*
import java.util.*

class MyCitiesListAdapter() : RecyclerView.Adapter<MyCitiesListAdapter.MyCitiesViewHolder>() {



    var items: List<CityCurrentWeatherTable> = LinkedList<CityCurrentWeatherTable>()

    val itemsCount = items.size

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
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun onBindViewHolder(holder: MyCitiesViewHolder, position: Int) {
        val item = items[position]
        holder.city_name.text = "" + R.string.city_name_field + item.city_name
        holder.country.text = "" + R.string.country_field + item.country
        holder.temp.text = "" + R.string.temp_field + item.temperature
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