package com.ceng.ozi.websocketchannelsample

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject

class LiveSocketListener(
    private val coroutineScope: CoroutineScope
) : WebSocketListener() {

    val socketEventChannel = Channel<SocketUpdate>(Channel.BUFFERED)

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        jsonArray.put("5~CCCAGG~BTC~USD")
        jsonObject.put("action", "SubAdd")
        jsonObject.put("subs", jsonArray)
        webSocket.send(jsonObject.toString())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        coroutineScope.launch {
            socketEventChannel.send(SocketUpdate(text = text))
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        coroutineScope.launch {
            socketEventChannel.send(SocketUpdate(exception = t))
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        coroutineScope.launch {
            socketEventChannel.send(SocketUpdate(exception = SocketAbortedException()))
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        socketEventChannel.close()
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

class SocketAbortedException : Exception()

data class SocketUpdate(
    val text: String? = null,
    val byteString: ByteString? = null,
    val exception: Throwable? = null
)
