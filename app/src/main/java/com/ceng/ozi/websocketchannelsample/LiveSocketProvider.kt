package com.ceng.ozi.websocketchannelsample

import com.ceng.ozi.websocketchannelsample.LiveSocketListener.Companion.NORMAL_CLOSURE_STATUS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


class LiveSocketProvider(
    private val coroutineScope: CoroutineScope
) {

    private var _webSocket: WebSocket? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private var _webSocketListener: LiveSocketListener? = null

    @ExperimentalCoroutinesApi
    fun startSocket(): Channel<SocketUpdate> =
        with(LiveSocketListener(coroutineScope)) {
            startSocket(this)
            this@with.socketEventChannel
        }

    @ExperimentalCoroutinesApi
    fun startSocket(webSocketListener: LiveSocketListener) {
        _webSocketListener = webSocketListener
        _webSocket = socketOkHttpClient.newWebSocket(
            Request.Builder().url(SOCKET_URL).build(),
            webSocketListener
        )
        socketOkHttpClient.dispatcher.executorService.shutdown()
    }

    @ExperimentalCoroutinesApi
    fun stopSocket() {
        try {
            _webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            _webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        const val READ_TIMEOUT: Long = 30L
        const val CONNECT_TIMEOUT: Long = 39L
        const val SOCKET_URL = "wss://streamer.cryptocompare.com/v2?api_key=773bb93ce05ee648aa1d0a73df0e576084e661848b8b3b092a9f327b0600b81e"
        const val API_KEY = "773bb93ce05ee648aa1d0a73df0e576084e661848b8b3b092a9f327b0600b81e"
    }
}