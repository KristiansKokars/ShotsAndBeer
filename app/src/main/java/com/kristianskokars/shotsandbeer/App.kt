package com.kristianskokars.shotsandbeer

import android.app.Application
import com.kristianskokars.shotsandbeer.common.LineNumberDebugTree
import com.kristianskokars.shotsandbeer.injection.DaggerInjectionComponent
import com.kristianskokars.shotsandbeer.injection.InjectionComponent
import com.kristianskokars.shotsandbeer.injection.InjectionModule
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree())
        }
        Timber.d("App created!")

        component = DaggerInjectionComponent
            .builder()
            .injectionModule(InjectionModule(this))
            .build()
    }

    companion object {
        lateinit var component: InjectionComponent
            private set
    }
}
