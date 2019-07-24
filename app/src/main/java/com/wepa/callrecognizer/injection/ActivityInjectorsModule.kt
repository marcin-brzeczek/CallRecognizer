package com.wepa.callrecognizer.injection

import com.wepa.callrecognizer.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityInjectorsModule {
    @ContributesAndroidInjector abstract fun provideMainActivityInjector(): MainActivity
}