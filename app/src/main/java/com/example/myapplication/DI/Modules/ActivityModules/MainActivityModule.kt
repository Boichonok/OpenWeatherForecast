package com.example.myapplication.DI.Modules.ActivityModules


import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.Application.WeatherForecastApplication
import com.example.myapplication.DI.Modules.ContextModule
import com.example.myapplication.DI.ScopeAnnotations.MainActivityScope
import com.example.myapplication.View.Adapters.MyCitiesListAdapter
import com.example.myapplication.View.CustomDialog.EnterMyCityDialog
import com.example.myapplication.View.MainActivity
import com.example.myapplication.View.ViewUtils.SwipeToDeleteCallback
import com.example.myapplication.ViewModel.IWeatherForecastViewModel
import com.example.myapplication.ViewModel.WeatherForecastViewModel
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {
    private val mainActivity: MainActivity
    private lateinit var swipeToDeleteCallback: SwipeToDeleteCallback


    constructor(mainActivity: MainActivity, swipeToDeleteCallback: SwipeToDeleteCallback){
        this.mainActivity = mainActivity
        this.swipeToDeleteCallback = swipeToDeleteCallback
    }


    @Provides
    @MainActivityScope
    fun getWeatherForecastViewModel(): IWeatherForecastViewModel
    {
        return ViewModelProviders.of(mainActivity).get(WeatherForecastViewModel::class.java)
    }


    @Provides
    @MainActivityScope
    fun getMyCitiesListAdapter(): MyCitiesListAdapter
    {
        return MyCitiesListAdapter()
    }

    @Provides
    @MainActivityScope
    fun getAddMyCitysDialogBuilder(): EnterMyCityDialog.Builder
    {
        return EnterMyCityDialog.Builder(this.mainActivity)
    }

    @Provides
    @MainActivityScope
    fun getItemTouchHelper(): ItemTouchHelper
    {
        return ItemTouchHelper(swipeToDeleteCallback)
    }

}