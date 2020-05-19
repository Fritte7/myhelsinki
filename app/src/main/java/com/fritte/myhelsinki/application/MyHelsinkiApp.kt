package com.fritte.myhelsinki.application

import android.app.Application
import com.fritte.myhelsinki.modules.NetworkAPI
import com.fritte.myhelsinki.modules.AppComponent
import com.fritte.myhelsinki.modules.DaggerAppComponent
import com.fritte.myhelsinki.modules.ModuleContext

class MyHelsinkiApp : Application() {

    companion object {
        lateinit var instance: MyHelsinkiApp private set
    }
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.builder()
                                         .moduleContext(ModuleContext(this))
                                         .networkAPI(NetworkAPI())
                                         .build()
        appComponent.inject(this)
    }

}