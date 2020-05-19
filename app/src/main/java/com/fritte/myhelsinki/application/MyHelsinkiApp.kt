package com.fritte.myhelsinki.application

import android.app.Application
import com.fritte.myhelsinki.module.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyHelsinkiApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyHelsinkiApp)
            modules(appModule)
        }
    }

}