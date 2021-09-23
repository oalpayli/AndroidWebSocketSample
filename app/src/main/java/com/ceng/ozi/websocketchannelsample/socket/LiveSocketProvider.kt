package com.ceng.ozi.websocketchannelsample.socket

import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface LiveSocketProvider {

    fun startSocket(webSocketListener: SocketConnectionListener)

    fun send(event: String)

    fun closeSocket()
}

@ExperimentalCoroutinesApi
@Singleton
class LiveSocketProviderImpl @Inject constructor() : LiveSocketProvider {

    private var _webSocket: WebSocket? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private var _webSocketListener: SocketConnectionListener? = null

    override fun startSocket(webSocketListener: SocketConnectionListener) {
        _webSocketListener = webSocketListener
        _webSocket = socketOkHttpClient.newWebSocket(
            Request.Builder().url(SOCKET_URL).build(),
            webSocketListener
        )
        socketOkHttpClient.dispatcher.executorService.shutdown()

        Log.d(this.javaClass.simpleName, "startSocket")
    }

    override fun send(event: String) {
        _webSocket?.send(event)
    }

    override fun closeSocket() {
        try {
            _webSocket?.close(SocketConnectionListener.NORMAL_CLOSURE_STATUS, null)
            _webSocket = null
            _webSocketListener = null
            Log.d(this.javaClass.simpleName, "closeSocket")
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.d(this.javaClass.simpleName, "closeSocket: Exception === ${ex.localizedMessage}")
        }
    }

    companion object {
        const val READ_TIMEOUT: Long = 30L
        const val CONNECT_TIMEOUT: Long = 39L
        const val SOCKET_URL =
            "wss://streamer.cryptocompare.com/v2?api_key=773bb93ce05ee648aa1d0a73df0e576084e661848b8b3b092a9f327b0600b81e"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LiveSocketProviderModule {

    @ExperimentalCoroutinesApi
    @Binds
    @Singleton
    abstract fun bindsLiveSocketProvider(
        liveSocketProviderImpl: LiveSocketProviderImpl
    ): LiveSocketProvider
}
