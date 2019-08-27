package com.example.myapplication.DI.Modules.ActivityModules

import com.example.myapplication.View.Adapters.PicassoMarker
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.Module
import dagger.Provides

@Module
class GoogleMapActivityModule {

    @Provides
    fun getMarkerOptions(): MarkerOptions
    {
        return MarkerOptions()
    }

    @Provides
    fun getPicassoMarker(): PicassoMarker
    {
        return PicassoMarker()
    }
}