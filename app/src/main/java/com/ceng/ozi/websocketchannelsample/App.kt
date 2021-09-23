package com.ceng.ozi.websocketchannelsample

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var socketLifecycleObserver: SocketLifecycleObserver

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(socketLifecycleObserver)
    }
}
