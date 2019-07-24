package com.wepa.callrecognizer.injection

import com.wepa.callrecognizer.InitApp
import com.wepa.callrecognizer.network.HeaderProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule (private val _application:InitApp){

    @Provides
    @Singleton
    fun provideApplication():InitApp = _application

    @Provides
    @Singleton
    fun provideAuthenticationHeader(): HeaderProvider = HeaderProvider()
}