package com.krish.headsup.utils

import android.content.Context

interface LocationCallback {
    fun onLocationSuccess(latitude: Double, longitude: Double)
    fun onLocationFailure()
}

class LocationUtils(private val context: Context, private val locationCallback: LocationCallback) {
    private val locationHelper = LocationHelper(context)

    fun getLiveLocation() {
        val locationTask = locationHelper.getLastLocation()
        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                locationCallback.onLocationSuccess(location.latitude, location.longitude)
            } else {
                locationCallback.onLocationFailure()
            }
        }.addOnFailureListener {
            locationCallback.onLocationFailure()
        }
    }
}
