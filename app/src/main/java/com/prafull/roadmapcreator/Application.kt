package com.prafull.roadmapcreator

import android.app.Application
import androidx.room.Room
import com.prafull.roadmapcreator.app.AppViewModel
import com.prafull.roadmapcreator.app.data.RoadmapCreatorDB
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(module)
        }
    }
}

val module = module {

    single<RoadmapCreatorDB> {
        Room.databaseBuilder(
            androidContext(),
            RoadmapCreatorDB::class.java,
            "roadmaps_db"
        ).build()
    }
    single {
        get<RoadmapCreatorDB>().roadmapDao()
    }
    viewModel {
        AppViewModel(get())
    }
}
