package com.kristianskokars.shotsandbeer

import android.app.Application
import com.kristianskokars.shotsandbeer.common.LineNumberDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree())
        }
    }
}
