package com.ceng.ozi.websocketchannelsample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ceng.ozi.websocketchannelsample.repository.MainRepository
import com.ceng.ozi.websocketchannelsample.socket.SocketEvent
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    @CryptoAdapter private val cryptoJsonAdapter: JsonAdapter<Crypto>,
    private val repository: MainRepository
) : ViewModel() {

    val updatedData = MutableStateFlow<Crypto?>(null)
    private val subscribeList = listOf("5~CCCAGG~BTC~USD")

    init {
        viewModelScope.launch {
            repository.subscribeTo(subscribeList)
        }
    }

    fun startGettingUpdate() {
        repository.message()
            .onEach {
                if (it is SocketEvent.OnMessageReceived) {
                    consumeUpdate(it.message)
                } else if (it is SocketEvent.OnFailureReceived) {
                    Log.d(
                        this@MainViewModel.javaClass.simpleName,
                        it.throwable.localizedMessage ?: ""
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun consumeUpdate(message: String) {
        updatedData.update {
            cryptoJsonAdapter.fromJson(message)
        }
    }

    override fun onCleared() {
        repository.unsubscribeFrom(subscribeList)
        super.onCleared()
    }
}
