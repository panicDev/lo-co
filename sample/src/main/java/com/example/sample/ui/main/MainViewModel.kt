package com.example.sample.ui.main

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationRequest
import id.panic_dev.android.loco.FusedLocation
import id.panic_dev.android.loco.getLocationUpdates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationProviderClient = FusedLocation.from(application)

    val textView = MutableLiveData("")

    private val onClickEventChannel = Channel<Unit>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val onClickEvent = onClickEventChannel.receiveAsFlow()

    fun onClick() {
        onClickEventChannel.trySend(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    fun startFetchLocation() {
        viewModelScope.launch(Dispatchers.Default) {
            locationProviderClient.getLocationUpdates {
                interval = 1000L
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }.shareIn(this, SharingStarted.WhileSubscribed(), 1).collect {
                textView.postValue(
                    "Lat : ${it.latitude}, Lon : ${it.longitude}"
                )
            }
        }
    }

}