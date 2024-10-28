package com.prafull.roadmapcreator

import android.app.Application
import com.prafull.roadmapcreator.app.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            loadKoinModules(
                module {
                    viewModel {
                        AppViewModel()
                    }
                }
            )
        }
    }
}