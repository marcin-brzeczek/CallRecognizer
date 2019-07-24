package com.wepa.callrecognizer

import com.wepa.callrecognizer.injection.ApiModule
import com.wepa.callrecognizer.injection.AppComponent
import com.wepa.callrecognizer.injection.ApplicationModule
import com.wepa.callrecognizer.injection.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasActivityInjector
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber

const val apiUrl: String = "http://ws.wepa.pl"

class InitApp : DaggerApplication(), HasActivityInjector {
    private val _applicationInjector:AppComponent by lazy {
        DaggerAppComponent.builder().let {
            it.seedInstance(this)
            it.setApplicationModule(ApplicationModule(this))
            it.setApiModule(ApiModule(this, apiUrl))
            it.build()
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = _applicationInjector

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        JodaTimeAndroid.init(this)
    }
}