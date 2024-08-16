package com.vino_tango.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.techtap.network.NetworkService
import com.techtap.network.OkHttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideGsonBuilder(): Gson {
//        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        return GsonBuilder().create()
    }

//    @Singleton
//    @Provides
//    internal fun provideNetworkService(application: Application): NetworkService {
//        return NetworkService(application)
//    }

    @Singleton
    @Provides
    internal fun provideNetworkService(
        application: Application,
        httpClientFactory: OkHttpClientFactory
    ): NetworkService {
        return NetworkService(context = application, okHttpClientFactory = httpClientFactory)
    }

    @Provides
    fun httpClient(): OkHttpClientFactory {
        return OkHttpClientFactory()
    }


}