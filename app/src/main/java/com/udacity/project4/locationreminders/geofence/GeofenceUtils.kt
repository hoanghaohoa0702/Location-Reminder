package com.udacity.project4.locationreminders.geofence

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.udacity.project4.R

fun getGeofenceErrorMessage(context: Context,errorCode: Int): String {
    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> context.getString(R.string.geofence_too_many_geofences)
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> context.getString(R.string.geofence_not_available)
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> context.getString(R.string.geofence_too_many_pending_intents)
        else -> context.getString(R.string.geofence_unknown_error)
    }
}