package com.fritte.myhelsinki.module

import com.fritte.myhelsinki.network.NetworkAPI
import com.fritte.myhelsinki.repositories.RepositoryDirection
import com.fritte.myhelsinki.repositories.RepositoryEvents
import com.fritte.myhelsinki.viewmodels.ViewModelEvents
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    val networkAPI = NetworkAPI()
    single { RepositoryEvents(networkAPI.provideMyHelsinkiAPI()) }
    single { RepositoryDirection(get(), networkAPI.provideGoogleAPI()) }

    viewModel { ViewModelEvents(get(), get()) }
}