package com.ceng.ozi.websocketchannelsample.repository

import com.ceng.ozi.websocketchannelsample.socket.LiveSocketProvider
import com.ceng.ozi.websocketchannelsample.socket.MessageEmitter
import com.ceng.ozi.websocketchannelsample.socket.SocketConnectionListener
import com.ceng.ozi.websocketchannelsample.socket.SocketEvent
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

interface MainRepository : MessageEmitter {

    fun subscribeTo(channels: List<String>)

    fun unsubscribeFrom(channels: List<String>)
}

@ExperimentalCoroutinesApi
@Singleton
class MainRepositoryImpl @Inject constructor(
    private val liveSocketProvider: LiveSocketProvider,
    private val socketConnectionListener: SocketConnectionListener
) : MainRepository {

    override fun subscribeTo(channels: List<String>) {
        if (channels.isEmpty())
            return

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        channels.forEach {
            jsonArray.put(it)
        }

        jsonObject.put("action", "SubAdd")
        jsonObject.put("subs", jsonArray)
        liveSocketProvider.send(jsonObject.toString())
    }

    //jsonArray.put("5~CCCAGG~BTC~USD")
    override fun unsubscribeFrom(channels: List<String>) {
        if (channels.isEmpty())
            return

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        channels.forEach {
            jsonArray.put(it)
        }

        jsonObject.put("action", "SubRemove")
        jsonObject.put("subs", jsonArray)
        liveSocketProvider.send(jsonObject.toString())
    }

    override fun message(): SharedFlow<SocketEvent> =
        socketConnectionListener.observe()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MainRepositoryModule {

    @ExperimentalCoroutinesApi
    @Binds
    @Singleton
    abstract fun bindsMainRepository(
        mainRepositoryImpl: MainRepositoryImpl
    ): MainRepository
}
