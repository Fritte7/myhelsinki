package com.fritte.myhelsinki.modules

import android.content.Context
import com.fritte.myhelsinki.application.MyHelsinkiApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModuleContext(private val app: MyHelsinkiApp) {

    @Provides
    @Singleton
    fun context(): Context {
        return app
    }
}