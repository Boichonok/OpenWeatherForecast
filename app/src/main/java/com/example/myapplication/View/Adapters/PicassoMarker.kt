package com.example.myapplication.View.Adapters

import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.squareup.picasso.Picasso
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Target
import java.lang.Exception


class PicassoMarker() : Target {

    private lateinit var mMarker: Marker

    fun setMarker(marker: Marker)
    {
        mMarker = marker
    }



    override fun hashCode(): Int {
        return mMarker.hashCode()
    }

    override fun equals(o: Any?): Boolean {
        if (o is PicassoMarker) {
            val marker = o.mMarker
            return mMarker == marker
        } else {
            return false
        }
    }



    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))

    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        Log.d("PicassoMarker","onBitmapFailed")
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        Log.d("PicassoMarker","onBitmapFailed: " + e!!.localizedMessage)
    }
}