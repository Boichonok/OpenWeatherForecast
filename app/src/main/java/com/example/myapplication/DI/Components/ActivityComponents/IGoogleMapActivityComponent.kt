package com.example.myapplication.DI.Components.ActivityComponents

import com.example.myapplication.DI.Modules.ActivityModules.GoogleMapActivityModule
import com.example.myapplication.View.GoogleMapActivity
import dagger.Component

@Component(modules = [GoogleMapActivityModule::class])
interface IGoogleMapActivityComponent {
    fun inject(googleMapActivity: GoogleMapActivity)
}