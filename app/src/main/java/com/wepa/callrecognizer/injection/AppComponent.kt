package com.wepa.callrecognizer.injection

import com.wepa.callrecognizer.InitApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ServiceInjectorsModule::class,
        AndroidSupportInjectionModule::class,
        ActivityInjectorsModule::class,
        ApplicationModule::class,
        ApiModule::class]
)

interface AppComponent : AndroidInjector<InitApp> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<InitApp>() {
        abstract fun setApplicationModule(module: ApplicationModule)
        abstract fun setApiModule(module: ApiModule)
        abstract override fun build(): AppComponent
    }
}