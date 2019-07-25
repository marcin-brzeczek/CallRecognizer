package com.wepa.callrecognizer.injection

import com.wepa.callrecognizer.call.CallDetectService
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
 abstract class ServiceInjectorsModule {
    @ContributesAndroidInjector
    internal abstract fun contributeMyService(): CallDetectService

}