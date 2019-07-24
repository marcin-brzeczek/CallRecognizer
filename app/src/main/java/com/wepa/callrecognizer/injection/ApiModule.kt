package com.wepa.callrecognizer.injection

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.wepa.callrecognizer.InitApp
import com.wepa.callrecognizer.network.ContactsApi
import com.wepa.callrecognizer.network.HeaderProvider
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule(val application: InitApp, val baseUrl: String) {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .addConverterFactory(JacksonConverterFactory.create(ObjectMapper().registerModule(KotlinModule())))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(headerProvider: HeaderProvider, cache: Cache) = OkHttpClient.Builder()
        .addInterceptor(headerProvider)
        .cache(cache)
        .build()

    @Provides
    @Singleton
    fun provideHttpCache(): Cache {
        val cacheSize = 10 * 1024 * 1024L
        return Cache(application.cacheDir, cacheSize)
    }

    @Provides
    fun provideContactsApi(retrofit: Retrofit) = retrofit.create(ContactsApi::class.java)
}