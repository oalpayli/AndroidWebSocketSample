package com.ceng.ozi.websocketchannelsample.socket

import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketConnectionListener @Inject constructor() : WebSocketListener() {

    private val eventFlow = MutableSharedFlow<SocketEvent>(
        replay = REPLAY_COUNT,
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    fun observe(): SharedFlow<SocketEvent> = eventFlow.asSharedFlow()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(this.javaClass.simpleName, "Socket onOpen")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        eventFlow.tryEmit(SocketEvent.OnMessageReceived(text))
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        eventFlow.tryEmit(SocketEvent.OnFailureReceived(t))
        Log.d(this.javaClass.simpleName, "Socket onFailure: ${t.localizedMessage}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        eventFlow.tryEmit(SocketEvent.OnFailureReceived(SocketAbortedException()))
        webSocket.close(NORMAL_CLOSURE_STATUS, reason)
        Log.d(this.javaClass.simpleName, "Socket onClosing: Code === $code")
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
        private const val BUFFER_CAPACITY: Int = 64
        private const val REPLAY_COUNT: Int = 1
    }
}

class SocketAbortedException : Exception()
