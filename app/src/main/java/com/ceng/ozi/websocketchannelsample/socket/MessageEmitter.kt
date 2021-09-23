package com.ceng.ozi.websocketchannelsample.socket

import kotlinx.coroutines.flow.SharedFlow

interface MessageEmitter {

    fun message(): SharedFlow<SocketEvent>
}