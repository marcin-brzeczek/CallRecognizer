package com.wepa.callrecognizer

import android.app.Service
import com.wepa.callrecognizer.injection.ApiModule
import com.wepa.callrecognizer.injection.AppComponent
import com.wepa.callrecognizer.injection.ApplicationModule
import com.wepa.callrecognizer.injection.DaggerAppComponent
import dagger.android.*
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import javax.inject.Inject

const val apiUrl: String = "http://ws.wepa.pl"

class InitApp : DaggerApplication(), HasActivityInjector, HasServiceInjector {
    private val _applicationInjector:AppComponent by lazy {
        DaggerAppComponent.builder().let {
            it.seedInstance(this)
            it.setApplicationModule(ApplicationModule(this))
            it.setApiModule(ApiModule(this, apiUrl))
            it.build()
        }
    }

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = _applicationInjector

    override fun serviceInjector(): DispatchingAndroidInjector<Service> = dispatchingServiceInjector

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        JodaTimeAndroid.init(this)
    }
}