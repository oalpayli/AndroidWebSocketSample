package com.ceng.ozi.websocketchannelsample.socket

sealed class SocketEvent {

    data class OnMessageReceived(val message: String) : SocketEvent()

    data class OnFailureReceived(val throwable: Throwable) : SocketEvent()
}
