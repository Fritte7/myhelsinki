package com.fritte.myhelsinki.modules

import com.fritte.myhelsinki.application.MyHelsinkiApp
import com.fritte.myhelsinki.repositories.RepositoryDirection
import com.fritte.myhelsinki.repositories.RepositoryEvents
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ModuleContext::class, NetworkAPI::class])
interface AppComponent {

    fun inject(app: MyHelsinkiApp)
    fun inject(repositoryDirection: RepositoryDirection)
    fun inject(repositoryEvents: RepositoryEvents)
}