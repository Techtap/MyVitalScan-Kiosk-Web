package com.techtap.network

import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthAuthenticator : Authenticator {
    private var isRequested = false

    override fun authenticate(route: Route?, response: Response): Request? {
        return null
//        val token = Pref.refreshToken
//        return runBlocking {
//            if (!isRequested) {
//                isRequested = true
//                val newToken = getNewToken(token)
//
//                if (!newToken.isSuccessful || newToken.body() == null) { //Couldn't refresh the token, so restart the login process
//                    Pref.accessToken = null
//                    Pref.refreshToken = null
//                    isRequested = false
//                }
//
//                newToken.body()?.let {
//                    isRequested = false
//                    Pref.accessToken = it.accessToken
//                    Pref.refreshToken = it.refreshToken
//                    response.request.newBuilder()
//                        .header("Authorization", "Bearer ${it.accessToken}")
//                        .build()
//                }
//            } else {
//                response.request
//            }
//        }
    }

//    private suspend fun getNewToken(refreshToken: String?): retrofit2.Response<LoginResponse> {
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(Url.HOST)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//        val service = retrofit.create(NetworkService.NetworkAPI::class.java)
//        return service.refreshToken(RefreshTokenRequest(refreshToken))
//    }
}