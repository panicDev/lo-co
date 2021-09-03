@file:Suppress("MemberVisibilityCanBePrivate")

package id.panic_dev.android.loco

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface FusedLocation {
    companion object {
        fun from(context: Context): FusedLocation {
            return from(
                locationProvider = lazy { LocationServices.getFusedLocationProviderClient(context.applicationContext) },
                settings = lazy { LocationServices.getSettingsClient(context.applicationContext) },
                geofencing = lazy { LocationServices.getGeofencingClient(context.applicationContext) },
            )
        }

        fun from(
            locationProvider: FusedLocationProviderClient,
            settings: SettingsClient,
            geofencing: GeofencingClient,
        ): FusedLocation = from(
            locationProvider = lazyOf(locationProvider),
            settings = lazyOf(settings),
            geofencing = lazyOf(geofencing),
        )

        fun from(
            locationProvider: Lazy<FusedLocationProviderClient>,
            settings: Lazy<SettingsClient>,
            geofencing: Lazy<GeofencingClient>,
        ): FusedLocation {
            return FusedLocationImpl(locationProvider, settings, geofencing)
        }
    }

    suspend fun checkLocationSettings(request: LocationSettingsRequest): LocationSettingsResponse

    suspend fun isLocationAvailable(request: LocationSettingsRequest): Boolean

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getCurrentLocation(priority: Int): Location?

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getLastLocation(): Location?

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getLocationUpdates(request: LocationRequest): Flow<Location>

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun setMockMode(isMockMode: Boolean)

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun setMockLocation(mockLocation: Location)

    suspend fun flushLocations()

    suspend fun addGeofences(request: GeofencingRequest, pendingIntent: PendingIntent)

    suspend fun removeGeofences(requestIds: List<String>)

    suspend fun removeGeofences(pendingIntent: PendingIntent)
}

suspend inline fun FusedLocation.checkLocationSettings(request: LocationRequest): LocationSettingsResponse {
    return checkLocationSettings { addLocationRequest(request) }
}

suspend inline fun FusedLocation.checkLocationSettings(
    builder: LocationSettingsRequest.Builder.() -> Unit,
): LocationSettingsResponse {
    return checkLocationSettings(LocationSettingsRequest.Builder().also { builder(it) }.build())
}

@ExperimentalCoroutinesApi
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
inline fun FusedLocation.getLocationUpdates(crossinline block: LocationRequest.() -> Unit): Flow<Location> {
    return getLocationUpdates(LocationRequest.create().apply(block))
}
