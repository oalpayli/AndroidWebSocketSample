package com.ceng.ozi.websocketchannelsample

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ceng.ozi.websocketchannelsample.socket.LiveSocketProvider
import com.ceng.ozi.websocketchannelsample.socket.SocketConnectionListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface SocketLifecycleObserver : LifecycleObserver {

    fun startConnection()

    fun closeConnection()
}

@Singleton
class SocketLifecycleObserverImpl @Inject constructor(
    private val liveSocketProvider: LiveSocketProvider,
    private val socketConnectionListener: SocketConnectionListener
) : SocketLifecycleObserver {


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun startConnection() {
        liveSocketProvider.startSocket(socketConnectionListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun closeConnection() {
        liveSocketProvider.closeSocket()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SocketLifecycleObserverModule {

    @Singleton
    @Binds
    abstract fun bindSocketLifecycleObserver(
        socketLifecycleObserverImpl: SocketLifecycleObserverImpl
    ): SocketLifecycleObserver
}
