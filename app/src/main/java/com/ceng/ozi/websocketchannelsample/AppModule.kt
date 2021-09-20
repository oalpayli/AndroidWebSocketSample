package com.ceng.ozi.websocketchannelsample

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .build()

    @Provides
    @Singleton
    @CryptoAdapter
    fun provideCryptoAdapter(moshi: Moshi): JsonAdapter<Crypto> =
        moshi.adapter(Crypto::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CryptoAdapter