package com.ceng.ozi.websocketchannelsample

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

class MainRepository constructor(private val liveSocketProvider: LiveSocketProvider) {

    @ExperimentalCoroutinesApi
    fun startSocket(): Channel<SocketUpdate> =
        liveSocketProvider.startSocket()

    @ExperimentalCoroutinesApi
    fun closeSocket() {
        liveSocketProvider.stopSocket()
    }
}
