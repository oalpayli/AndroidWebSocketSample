package com.ceng.ozi.websocketchannelsample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    @CryptoAdapter private val cryptoJsonAdapter: JsonAdapter<Crypto>
) : ViewModel() {

    private val liveSocketProvider = LiveSocketProvider(viewModelScope)

    private val repository = MainRepository(liveSocketProvider)

    val updatedData = MutableStateFlow<Crypto?>(null)

    fun startGettingUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startSocket().consumeEach {
                it.exception?.let { throwable ->
                    Log.d("ozi", throwable.localizedMessage ?: "exception is null")
                } ?: consumeUpdate(it)
            }
        }
    }

    private fun consumeUpdate(socketUpdate: SocketUpdate) {
        socketUpdate.text?.let { updatedValue ->
            updatedData.update {
                cryptoJsonAdapter.fromJson(updatedValue)
            }
        }
    }

    override fun onCleared() {
        repository.closeSocket()
        super.onCleared()
    }
}
