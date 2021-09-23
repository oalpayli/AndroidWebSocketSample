package com.ceng.ozi.websocketchannelsample.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ceng.ozi.websocketchannelsample.MainViewModel
import com.ceng.ozi.websocketchannelsample.databinding.FragmentCryptoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CryptoFragment : Fragment() {

    private lateinit var viewBinding: FragmentCryptoBinding

    private val viewModel: MainViewModel by viewModels()

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentCryptoBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.startGettingUpdate()
        }
    }

    override fun onResume() {
        super.onResume()
        job = lifecycleScope.launch {
            viewModel.updatedData
                .filterNotNull()
                .collectLatest { crypto ->
                    crypto.price?.let {
                        viewBinding.tvCryptoValue.text = it.toString()
                    }
                }
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
        job = null
    }
}
