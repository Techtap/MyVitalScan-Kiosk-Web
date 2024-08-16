package com.techtap.network

import com.techtap.BuildConfig
import com.techtap.network.NetworkService
import com.techtap.network.NetworkUtils
import com.techtap.utils.Constant
import com.techtap.utils.Pref
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class OkHttpClientFactory {
    private var okHttpClient: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = instantiateOkHttpClient()
        }
        return okHttpClient!!
    }

    private fun instantiateOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        // Custom Dns to avoid ipv6 incompatibility with some Android devices
        //  builder.dns(new PreferIpv4Dns());
        builder.addInterceptor { chain ->
            //this is where we will add whatever we want to our request headers.
            val basicRequest = chain.request()
            val requestBuilder = basicRequest.newBuilder()

            /**
             * Option Header
             */

            /**
             * Option Header
             */
//            if (NetworkUtils.isNotEmpty(basicRequest.header(NetworkService.HEADER_BEAR))) {
//                // fall back to basic if there is no bearer token
//                val authorizationValue =
//                    if (NetworkUtils.getAuthParams().containsKey(NetworkUtils.AUTHORIZATION))
//                        NetworkUtils.getAuthParams()[NetworkUtils.AUTHORIZATION]
//                    else
//                        NetworkUtils.onboardEncodeBase64Header(":")
//                requestBuilder.addHeader(NetworkService.HEADER_AUTHORIZATION, authorizationValue!!)
//                requestBuilder.removeHeader(NetworkService.HEADER_BEAR)
//            } else if (NetworkUtils.isNotEmpty(basicRequest.header(NetworkService.HEADER_BASIC))) {
//                requestBuilder.addHeader(
//                    NetworkService.HEADER_AUTHORIZATION,
//                    NetworkUtils.onboardEncodeBase64Header(":")
//                )
//                requestBuilder.removeHeader(NetworkService.HEADER_BASIC)
//            } else if (NetworkUtils.isNotEmpty(basicRequest.header(NetworkService.HEADER_OPTION))) {
//                //Will improve
//                val oAuthToken = Pref.accessToken
//                if (oAuthToken != null) {
//                    requestBuilder.addHeader(
//                        NetworkService.HEADER_AUTHORIZATION,
//                        (NetworkUtils.getAuthParams()[NetworkUtils.AUTHORIZATION]!!)
//                    )
//                    requestBuilder.removeHeader(NetworkService.HEADER_OPTION)
//                } else {
//                    requestBuilder.addHeader(
//                        NetworkService.HEADER_AUTHORIZATION,
//                        NetworkUtils.onboardEncodeBase64Header(":")
//                    )
//                    requestBuilder.removeHeader(NetworkService.HEADER_OPTION)
//                }
//            }

            /**
             * Default Header
             */

            /**
             * Default Header
             */
            requestBuilder.addHeader(
                NetworkService.HEADER_USER_AGENT, NetworkUtils.APP_PLATFORM_USER_AGENT
            )
//            requestBuilder.addHeader(NetworkService.HEADER_APP_VERSION, BuildConfig.API_VERSION)
//            requestBuilder.addHeader(
//                NetworkService.HEADER_APP_VERSION_NAME, BuildConfig.APP_VERSION
//            )
//            requestBuilder.addHeader(NetworkService.HEADER_API_VERSION, BuildConfig.API_VERSION)


            //Added Header
//            val authorizationValue =
//                if (NetworkUtils.getAuthParams().containsKey(NetworkUtils.AUTHORIZATION))
//                    NetworkUtils.getAuthParams()[NetworkUtils.AUTHORIZATION]
//                else
//                    NetworkUtils.onboardEncodeBase64Header(":")
//            requestBuilder.addHeader(NetworkService.HEADER_AUTHORIZATION, authorizationValue!!)

//            requestBuilder.addHeader("locale", Pref.appLocale)
//            requestBuilder.addHeader("Content", "application/json")
            requestBuilder.addHeader("Accept", "application/json")
            requestBuilder.addHeader("Content-Type", "application/json")
            requestBuilder.addHeader("x-platform", "APP")
            requestBuilder.addHeader("x-os", "ANDROID")
            requestBuilder.addHeader("x-timezone", TimeZone.getDefault().id)
//            if (BuildConfig.FLAVOR.equals(Constant.INTERNAL, true)) {
//                requestBuilder.addHeader("platform", "PI-INTERNAL")
//            } else{
//                requestBuilder.addHeader("platform", "PI-EXTERNAL")
//            }
            if (BuildConfig.FLAVOR.equals(Constant.INTERNAL, true)) {
                requestBuilder.addHeader("platform", "KIOSK_PI_INTERNAL")
            } else {
                requestBuilder.addHeader("platform", "KIOSK_PI_EXTERNAL")
            }


            // For guest user
//            val uuid = Pref.uuid
//            if (!uuid.isNullOrEmpty()) {
//                requestBuilder.addHeader(NetworkService.HEADER_UUID, uuid)
//            }
            chain.proceed(requestBuilder.build())
        }
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }

        return builder.build()
    }
}
