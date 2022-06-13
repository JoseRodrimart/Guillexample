package com.guille.guillexample.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class IotViewModel(application: Application) : AndroidViewModel(application) {

    var iotNotifications = MutableLiveData<Int>(0)

    fun increaseNumber(){
        iotNotifications.value = iotNotifications.value?.inc()
    }
}