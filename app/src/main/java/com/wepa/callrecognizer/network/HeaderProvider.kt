package com.wepa.callrecognizer.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val username = "7372115358"

private const val password = "232671423"

@Singleton
class HeaderProvider @Inject constructor() : Interceptor {

    private val credentials = Credentials.basic(username, password)

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().newBuilder().addHeader("Authorization", credentials).build())
    }
}