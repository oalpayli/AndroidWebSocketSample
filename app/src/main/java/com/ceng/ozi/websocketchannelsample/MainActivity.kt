package com.ceng.ozi.websocketchannelsample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ceng.ozi.websocketchannelsample.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        lifecycleScope.launchWhenCreated {
            viewModel.startGettingUpdate()
        }
    }

    override fun onResume() {
        super.onResume()
        job = lifecycleScope.launch {
            viewModel.updatedData.collectLatest {
                viewBinding.tvCryptoValue.text = it?.price?.toString()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
        job = null
    }
}